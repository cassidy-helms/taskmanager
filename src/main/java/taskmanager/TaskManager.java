package taskmanager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskService;
import taskmanager.util.enums.Action;
import taskmanager.util.enums.Status;



public class TaskManager {
	private static final DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final String DUE_DATE_PATTERN = "yyyy-MM-dd";
	private static final String YES = "y";
	private static final String NO = "n";
	private static final String EXIT = "0";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";

	private final TaskService taskService;
	private final Scanner scanner;
	private List<Task> allTasks;

	public TaskManager(TaskService taskService, Scanner scanner) {
		this.taskService = taskService;
		this.scanner = scanner;
		this.allTasks = taskService.getAllTasks();
	}

	public static void main(String[] args) {
		TaskRepository repository = new TaskRepository();
		TaskService service = new TaskService(repository);
		Scanner scanner = new Scanner(System.in);
		TaskManager app = new TaskManager(service, scanner);
		app.run();
	}

	public void run() {
		System.out.println("Welcome to the Task Manager.");
		String input = "";
		do {
			printMenu();
			input = scanner.nextLine();
			Action action = Action.lookupActionById(input);
			
			if(action == null) {
				invalidInput();
				continue;
			}
			
			switch (action) {
				case VIEW -> viewTasks();
				case ADD -> addTask();
				case MARK_COMPLETE -> markTaskComplete();
				case UPDATE -> updateTask();
				case REMOVE -> removeTask();
				case CLEAN_UP -> cleanUpTasks();
				case SAVE -> saveTasks();
				case EXIT -> {
					if(exit()) return;
					else {
						input = "";
						break;
					}
				}
			}
		} while(!input.equals(Action.EXIT.getId()));
		scanner.close();
	}


	private void printMenu() {
		System.out.println("\nTask Quickview:");
		System.out.println(Status.TODO.getName() + ": " + taskService.findTasksByStatus(Status.TODO).size());
		System.out.println(Status.IN_PROGRESS.getName() + ": " + taskService.findTasksByStatus(Status.IN_PROGRESS).size());
		System.out.println(Status.COMPLETED.getName() + ": " + taskService.findTasksByStatus(Status.COMPLETED).size());
		System.out.println("Overdue Tasks: " + taskService.findOverdueTasks().size());
		
        System.out.println("\nSelect which option you would like to do:");
        for (Action action : Action.values()) {
        	System.out.println(action.getId() + ": " + action.getLongName());
        }
        System.out.print("Option: ");
    }
	
	/**
	 * Entry Point to View Tasks Menu
	 */
	private void viewTasks() {
		String input = "";
		
		printTasks();
		
		do {
			System.out.println("\nDo you want to: ");
			System.out.println("1. View Tasks by Status");
			System.out.println("2. View All Incomplete Tasks");
			System.out.println("3. View Overdue Tasks");
			System.out.println(EXIT + ". Return to Main Menu\n");

			input = userInput();
			
			switch(input) {
				case "1" -> printTasks(taskService.findTasksByStatus(readInStatus()));
				case "2" -> printTasks(taskService.findIncompleteTasks());
				case "3" -> printTasks(taskService.findOverdueTasks());
    			case EXIT -> {
					return;
				} 
				default -> invalidInput();
			}
			
		} while(!input.isEmpty());
	}
	
	/**
	 * Entry Point to Add Tasks Menu
	 */
	private void addTask() {
		System.out.println("\nAdding a new task...");
		
		String title = readInTitle();
		String description = readInDescription();
		LocalDate dueDate = readInDate();
		Status status = readInStatus();
		
		Task task = new Task(title, description, dueDate, status);
		if(confirmAction(Action.ADD, task)) {
			System.out.println("Adding Task...");
			taskService.addTask(task);
		} else System.out.println("Discarding Task...");
		
		if(!returnToMenu(Action.ADD)) addTask();
	}
	
	/**
	 * Entry Point to Mark Tasks Complete Menu
	 */
	private void markTaskComplete() {
		if(hasNoTasks()) return;
			
		List<Task> incompleteTasks = taskService.findIncompleteTasks();
		List<Integer> taskIds = selectTasks(Action.MARK_COMPLETE, incompleteTasks);
		
		if(taskIds.isEmpty()) return;
		
		taskIds = getTaskIndiciesFromSubList(taskIds, incompleteTasks);
		
		if(confirmAction(Action.MARK_COMPLETE, getTasksFromIds(taskIds))) {
			for(int id : taskIds) {
				Task task = allTasks.get(id);
				Task taskToComplete = new Task(task.getTitle(), task.getDescription(), task.getDueDate(), Status.COMPLETED);
				taskService.updateTask(id, taskToComplete);
			}
			
			System.out.println("Tasks marked as complete!");
		}
		
		if(!returnToMenu(Action.MARK_COMPLETE)) markTaskComplete();
	}
	
	/**
	 * Entry Point to Update Tasks Menu
	 */
	private void updateTask() {
		if(hasNoTasks()) return;
		
		int taskId = selectTask(Action.UPDATE);
		if(taskId == -1) return;
		
		Task task = allTasks.get(taskId);
		Task taskToUpdate = new Task(task.getTitle(), task.getDescription(), task.getDueDate(), task.getStatus());
		
		String input = "";
		boolean saved = false;
		
		do {
			System.out.println("\nSelected Task:");
			System.out.println("1. Current Title: " + taskToUpdate.getTitle());
			System.out.println("2. Current Description: " + (taskToUpdate.getDescription() == null ? "" : taskToUpdate.getDescription()));
			System.out.println("3. Current Due Date: " + (taskToUpdate.getDueDate() == null ? "" : taskToUpdate.getDueDate()));
			System.out.println("4. Current Status: " + taskToUpdate.getStatus().getName());
			System.out.println("5. Finish Updating Task");
			System.out.println(EXIT + ". Return to Main Menu");
			
			System.out.println("Which field would you like to update?:");
			input = userInput();
			
			switch(input) {
				case "1" -> taskToUpdate.setTitle(readInTitle());
				case "2" -> taskToUpdate.setDescription(readInDescription());
				case "3" -> taskToUpdate.setDueDate(readInDate());
				case "4" -> taskToUpdate.setStatus(readInStatus());
				case "5" ->  {
					break;
				}
				case EXIT -> {
					if (!saved) {
						System.out.println("Update not yet complete!");
						if(returnToMenu()) return;
					}
				}
				default -> invalidInput();
			}
		} while(!input.equals("5"));
		
		if(confirmAction(Action.UPDATE, taskToUpdate)) {
			System.out.println("Updating task...");
			taskService.updateTask(taskId, taskToUpdate);
		} else {
			System.out.println("Discarding updates...");
		}
		
		if(!returnToMenu(Action.UPDATE)) updateTask();
	}
	
	/**
	 * Entry Point to Remove Tasks Menu
	 */
	private void removeTask() {
		if(hasNoTasks()) return;
		
		List<Integer> taskIds = selectTasks(Action.REMOVE);
		
		if(taskIds.isEmpty()) return;
		
		if(confirmAction(Action.REMOVE, getTasksFromIds(taskIds))) {
			taskService.removeTasksById(taskIds);
			System.out.println("Task(s) removed!");
		} else {
			System.out.println("Keeping Tasks...");
		}
		
		if(!returnToMenu(Action.REMOVE)) removeTask();
	}
	
	/**
	 * Entry Point to Clean Up Tasks Menu
	 */
	private void cleanUpTasks() {
		if(hasNoTasks()) return;
		
		printTasks(taskService.findTasksByStatus(Status.COMPLETED));
		String input = "";
		do {
			System.out.println("\nDo you want to: ");
			System.out.println("1. Remove ALL Completed Tasks");
			System.out.println("2. Remove Completed Tasks On or Before Given Date");
			System.out.println(EXIT + ". Return to Main Menu\n");

			input = userInput();
			List<Task> completedTasks = taskService.findTasksByStatus(Status.COMPLETED);
			
			switch(input) {
				case "1" -> {
					if(confirmAction(Action.CLEAN_UP, completedTasks)) taskService.removeTasks(completedTasks);
				}
				case "2" -> {
					List<Task> completedTasksBeforeDate = taskService.findTasksOnOrBeforeDate(readInDate(false), completedTasks);
					if(completedTasksBeforeDate.isEmpty()) System.out.println("\nNo Completed Tasks to Clean Up!");
					else if(confirmAction(Action.CLEAN_UP, completedTasksBeforeDate)) taskService.removeTasks(completedTasksBeforeDate);
				}
				case EXIT -> {
					return;
				}
				default -> invalidInput();
			}
			
		} while(!input.isEmpty());
	}
	
	/**
	 * Entry Point to Save Tasks Menu
	 */
	private void saveTasks() {
		if(confirmAction(Action.SAVE)) {
			System.out.println("Saving tasks...");
			taskService.saveTasks();
			System.out.println("Tasks saved!");
		}
	}
	
	private String readInTitle() {
		String title = "";
		
		while(title.isEmpty()) {
			System.out.println("Enter the Title: ");
			title = userInput();
			
			if(title.isEmpty()) {
				System.out.println("Title is a requiered field.");
			}
		}
		
		return title;
	}
	
	private String readInDescription() {
		System.out.println("Enter the Description (or just press Enter if no Description needed):");
		return userInput();
	}
	
	private LocalDate readInDate(boolean forDueDate) {
		if(forDueDate) System.out.println("Enter the Due Date (or just press Enter if no Due Date needed):");
		else System.out.println("Enter Date:");
		System.out.println("Please use date format " + DUE_DATE_PATTERN);
		
		String dateString = userInput();
		LocalDate date = null;
		
		if(!dateString.isEmpty()) {
			do {
				try {
					date = LocalDate.parse(dateString, dueDateFormat.withResolverStyle(ResolverStyle.STRICT));
					
					if(forDueDate && date.isBefore(LocalDate.now())) {
						System.out.println("Due date has already passed! Please enter a valid date using format " + DUE_DATE_PATTERN);
						dateString = userInput();
						date = null;
					}
				} catch(DateTimeParseException e) {
					System.out.println("Please use date format " + DUE_DATE_PATTERN);
					dateString = userInput();
				}
			} while(date == null && !dateString.isEmpty());
		}
		
		return date;
	}
	
	private LocalDate readInDate() {
		return readInDate(true);
	}
	
	private Status readInStatus() {
		String statusId = "";
		Status status = null;
		
		do {
			System.out.println("\nSelect the proper status:");
			for(Status s : Status.values()) {
				System.out.println(s.getId() + ": " + s.getName());
			}
			statusId = userInput();
			status = Status.lookupStatusById(statusId);
		} while (status == null);
		
		return status;
	}
	
	/**
	 * Confirm action from the user to determine if changes should be kept or discarded
	 * 
	 * @param	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @param	tasks	a List of Tasks that the action could be taken on
	 * @return			boolean - true if the user confirms the action or false if the changes are to be discarded
	 */
	private boolean confirmAction(Action action, List<Task> tasks) {
		if(action == Action.EXIT) System.out.print("Are you sure you want to exit? ");
		else System.out.print("\nDo you want to " + action.getShortName() + " the " + (tasks.isEmpty() ? "" : "below ") + "task(s)? ");
		System.out.println("Enter " + YES + " for Yes and " + NO + " for No.");
		if(!tasks.isEmpty())printTasks(tasks);
			
		String input = userInput();
		
		while(!input.equalsIgnoreCase(YES) && !input.equalsIgnoreCase(NO)) {
			System.out.println("\nInvalid input.  Please enter " + YES + " to " + action.getShortName() + " the task(s) or " + NO + " to go back.");
			input = userInput();
		}
		
		return input.equalsIgnoreCase(YES);
	}
	
	/**
	 * Confirm action from the user to determine if changes should be kept or discarded
	 * 
	 * @param	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @param	task	a Task that the action could be taken on
	 * @return			boolean - true if the user confirms the action or false if the changes are to be discarded
	 */
	private boolean confirmAction(Action action, Task task) {
		return confirmAction(action, Arrays.asList(task));
	}
	
	/**
	 * Confirm action from the user to determine if changes should be kept or discarded
	 * 
	 * @param	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @return			boolean - true if the user confirms the action or false if the changes are to be discarded
	 */
	private boolean confirmAction(Action action) {
		return confirmAction(action, new ArrayList<>());
	}
	
	/**
	 * Checks if the user wants to perform an additional action of the same type or return to the Main Menu
	 * @param 	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @return			boolean - true if the user wants to return to the Main Menu or false to perform an additional action of the same type
	 */
	private boolean returnToMenu(Action action) {
		System.out.println("\nDo you want to " + action.getShortName() + " another task? Enter " + YES + " for Yes or " + NO + " to return to the Main Menu");
		
		String input = userInput();
		
		while(!input.equalsIgnoreCase(YES) && !input.equalsIgnoreCase(NO)) {
			System.out.println("\nInvalid input.  Please enter " + YES + " to " + action.getShortName() + " a task or " + NO + " to return to the Main Menu");
			input = userInput();
		}
		
		if(input.equalsIgnoreCase(NO)) {
			saveReminder();
		}
		
		return input.equalsIgnoreCase(NO);
	}
	
	/**
	 * Confirms that the user wants to return to the Main Menu
	 * @return			boolean - true if the user wants to return to the Main Menu or false to remain in their current location
	 */
	private boolean returnToMenu() {
		System.out.println("\nYou are trying to return to the Main Menu. Enter " + YES + " to Stay Here or " + NO + " to return to the Main Menu");
		
		String input = userInput();
		
		while(!input.equalsIgnoreCase(YES) && !input.equalsIgnoreCase(NO)) {
			System.out.println("\nInvalid input.  Please enter" + YES + " to stay here or " + NO + " to return to the Main Menu");
			input = userInput();
		}
		
		if(input.equalsIgnoreCase(NO)) {
			saveReminder();
		}
		
		return input.equalsIgnoreCase(NO);
	}
	
	/**
	 * Confirms that the user wants to exit the program.  If the task list has not been changed, the user can automatically exit.  Otherwise, it confirms that the user actually wants to exit.
	 * @return			boolean - true if the user wants to exit the program or false if they do not
	 */
	private boolean exit() {
		if (!taskService.hasTaskListChanged()) return true;
		
		System.out.println("Unsaved changes!");
		return confirmAction(Action.EXIT);
	}
	
	private void saveReminder() {
		System.out.println("Reminder: Tasks are not saved automatically. Please remember to save before exiting the program.\n");
	}
	
	private void invalidInput() {
		System.out.println("\nInvalid Input. Please try again.");
	}
	
	private boolean hasNoTasks() {
		if(allTasks.isEmpty()) System.out.println("\nTask List is Empty! Returning to Main Menu!");
			
		return allTasks.isEmpty();
	}
	
	/**
	 * Allows the user to select which tasks they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @param 	allowMultiple	a boolean, if true, that allows the user to select multiple tasks at once
	 * @param 	tasks			a List of Tasks that the action could be taken on
	 * @return					a List of Task Ids that the user wants to perform the action on
	 */
	private List<Integer> selectTasks(Action action, boolean allowMultiple, List<Task> tasks) {
		System.out.println("\nWhich task(s) do you want to " + action.getShortName());
		if(tasks == null) printTasks();
		else printTasks(tasks);
		System.out.println(EXIT + ". Return to Main Menu");
		
		List<Integer> indicies = new ArrayList<>();
		
		do {
			System.out.println("Enter the id of the task you wish to select" + (allowMultiple ? ". You may select multiple tasks at once by separating them with a comma:" : ":"));
			String input = userInput();
			List<String> splitInput = Arrays.stream(input.split(","))
					  .map(String::trim)
					  .toList();
			
			try {
				if(splitInput.contains(EXIT)) {
					if(splitInput.size() == 1 || returnToMenu()) return indicies;
				}
				
				for(String s : splitInput) {
					int index = Integer.parseInt(s) - 1;
					
					if(index < 0 || index >= tasks.size()) throw new Exception("Invalid Task Id");
					else indicies.add(index);
				}
			} catch (Exception e) {
				invalidInput();
				indicies.clear();
			}
		} while(indicies.isEmpty());
		
		return indicies;
	}
	
	/**
	 * Allows the user to select which tasks they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @param 	tasks			a List of Tasks that the action could be taken on
	 * @return					a List of Task Ids that the user wants to perform the action on
	 */
	private List<Integer> selectTasks(Action action, List<Task> tasks) {
		return selectTasks(action, true, tasks);
	}
	
	/**
	 * Allows the user to select which tasks they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @return					a List of Task Ids that the user wants to perform the action on
	 */
	private List<Integer> selectTasks(Action action) {
		return selectTasks(action, true, allTasks);
	}
	
	/**
	 * Allows the user to select which task they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @return					the task id of the Task the user wants to perform the action on or -1 if the user does not wish to proceed
	 */
	private int selectTask(Action action) {
		List<Integer> taskIds = selectTasks(action, false, allTasks);
		
		return taskIds.isEmpty() ? -1 : taskIds.getFirst();
	}
	
	private List<Integer> getTaskIndiciesFromSubList(List<Integer> indicies, List<Task> tasks) {
		return indicies.stream().map(i -> allTasks.indexOf(tasks.get(i))).collect(Collectors.toList());
	}
	
	private List<Task> getTasksFromIds(List<Integer> taskIds) {
		return taskIds.stream().map(id -> allTasks.get(id)).collect(Collectors.toList());
	}

	private void printTasks(List<Task> tasks) {
		if(tasks.isEmpty()) System.out.println("\nYou currently have no tasks.");
		else {
			System.out.println("\nTasks: ");
			IntStream.range(0, tasks.size()).forEach(i -> {
				Task task = tasks.get(i);
				System.out.println((task.isOverdue() ? ANSI_RED : "") + (i + 1) + ": " + tasks.get(i) + (task.isOverdue() ? ANSI_RESET : ""));
			});
		}
	}
	
	private void printTasks() {
		printTasks(allTasks);
	}
	
	private String userInput() {
		System.out.print("-> ");
		return scanner.nextLine();
	}
}
