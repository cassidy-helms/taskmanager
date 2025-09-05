package main.java.taskmanager;

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

import main.java.taskmanager.model.Task;
import main.java.taskmanager.repository.TaskRepository;
import main.java.taskmanager.service.TaskService;
import main.java.taskmanager.util.enums.Action;
import main.java.taskmanager.util.enums.Status;


public class TaskManager {	
	private static final DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final String DUE_DATE_PATTERN = "yyyy-MM-dd";
	private static final String YES = "y";
	private static final String NO = "n";
	private static final String EXIT = "0";
	private static final TaskService taskService;
	private static final Scanner scanner;
	private static List<Task> allTasks;
	
	static {
		taskService = new TaskService();
		scanner = new Scanner(System.in);
		allTasks = taskService.getAllTasks();
	}
	
	public static void main(String[] args) {		
		System.out.println("Welcome to the Task Manager.");
		
		String input = "";
		
		do {
			System.out.println("\nSelect which option you would like to do:");
			for(int i = 0; i < Action.values().length; i++) {
				System.out.println((i + 1) + ": " + Action.values()[i].getLongName());
			}
			
			System.out.print("Option: ");
			
			input = scanner.nextLine();
			Action action = Action.lookupActionById(input);
			
			switch(action) {
				case Action.VIEW:
					viewTasks();
					break;
				case Action.ADD:
					addTask();
					break;
				case Action.MARK_COMPLETE:
					markTaskComplete();
					break;
				case Action.UPDATE:
					updateTask();
					break;
				case Action.REMOVE:
					removeTask();
					break;
				case Action.SAVE:
					saveTasks();
					break;
				case Action.EXIT:
					// Exit
					return;
				case null:
				default:
					System.out.println("Invalid input.  Please try again.");
			}
		} while(!input.equals(Action.EXIT.getId()));
		
		scanner.close();
	}
	
	/**
	 * Entry Point to View Tasks Menu
	 */
	private static void viewTasks() {
		String input = "";
		do {
			System.out.println("\nDo you want to: ");
			System.out.println("1. View All Tasks");
			System.out.println("2. View Tasks by Status");
			System.out.println(EXIT + ". Return to Main Menu\n");

			input = scanner.nextLine();
			
			switch(input) {
				case "1":
					System.out.println("\nTasks: ");
					taskService.printTasks();
					break;
				case "2":
					Status status = readInStatus();
					System.out.println("\nTasks: ");
					if(status != null) taskService.printTasks(taskService.getAllTasksByStatus(status));
					break;
				case EXIT:
					return;
				default:
					System.out.println("Invalid input. Please try again.");
			}
			
		} while(!input.isEmpty());
	}
	
	/**
	 * Entry Point to Add Tasks Menu
	 */
	private static void addTask() {
		System.out.println("\nAdding a new task...");
		
		String title = readInTitle();
		String description = readInDescription();
		LocalDate dueDate = readInDate();
		Status status = readInStatus();
		
		Task task = new Task(title, description, dueDate, status);
		if(confirmAction(Action.ADD.getShortName(), task)) {
			System.out.println("Adding Task...");
			taskService.addTask(task);
		} else System.out.println("Discarding Task...");
		
		if(!returnToMenu(Action.ADD.getShortName())) addTask();
	}
	
	/**
	 * Entry Point to Mark Tasks Complete Menu
	 */
	private static void markTaskComplete() {
		if(hasNoTasks()) return;
			
		List<Task> incompleteTasks = allTasks.stream().filter(task -> !task.getStatus().equals(Status.COMPLETED)).collect(Collectors.toList());
		List<Integer> taskIds = selectTasks(Action.MARK_COMPLETE.getShortName(), incompleteTasks);
		
		if(taskIds.isEmpty()) return;
		
		if(confirmAction(Action.MARK_COMPLETE.getShortName(), getTasksFromIds(taskIds))) {
			for(int id : taskIds) {
				Task taskToComplete = allTasks.get(id);
				taskToComplete.setStatus(Status.COMPLETED);
				taskService.updateTask(id, taskToComplete);
			}
			
			System.out.println("Tasks marked as complete!");
		}
		
		if(!returnToMenu(Action.MARK_COMPLETE.getShortName())) markTaskComplete();
	}
	
	/**
	 * Entry Point to Update Tasks Menu
	 */
	private static void updateTask() {
		if(hasNoTasks()) return;
		
		int taskId = selectTask(Action.UPDATE.getShortName());
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
			input = scanner.nextLine();
			
			switch(input) {
				case "1":
					taskToUpdate.setTitle(readInTitle());
					break;
				case "2":
					taskToUpdate.setDescription(readInDescription());
					break;
				case "3":
					taskToUpdate.setDueDate(readInDate());
					break;
				case "4":
					taskToUpdate.setStatus(readInStatus());
					break;
				case "5":
					break;
				case EXIT:
					if (!saved) {
						System.out.println("Update not yet complete!");
						if(returnToMenu()) return;
					}
					break;
				default:
					System.out.println("Invalid input. Please try again.");
			}
		} while(!input.equals("5"));
		
		if(confirmAction(Action.UPDATE.getShortName(), taskToUpdate)) {
			System.out.println("Updating task...");
			taskService.updateTask(taskId, taskToUpdate);
		} else {
			System.out.println("Discarding updates...");
		}
		
		if(!returnToMenu(Action.UPDATE.getShortName())) updateTask();
	}
	
	/**
	 * Entry Point to Remove Tasks Menu
	 */
	private static void removeTask() {
		if(hasNoTasks()) return;
		
		List<Integer> taskIds = selectTasks(Action.REMOVE.getShortName());
		
		if(taskIds.isEmpty()) return;
		
		if(confirmAction(Action.REMOVE.getShortName(), getTasksFromIds(taskIds))) {
			taskService.removeTasks(taskIds);
			System.out.println("Task(s) removed!");
		} else {
			System.out.println("Keeping Tasks...");
		}
		
		if(!returnToMenu(Action.REMOVE.getShortName())) removeTask();
	}
	
	
	/**
	 * Entry Point to Save Tasks Menu
	 */
	private static void saveTasks() {
		if(confirmAction(Action.SAVE.getShortName())) {
			System.out.println("Saving tasks...");
			TaskRepository.saveTasks(allTasks);
			System.out.println("Tasks saved!");
		}
	}
	
	private static String readInTitle() {
		String title = "";
		
		while(title.isEmpty()) {
			System.out.println("Enter the Title: ");
			title = scanner.nextLine();
			
			if(title.isEmpty()) {
				System.out.println("Title is a requiered field.");
			}
		}
		
		return title;
	}
	
	private static String readInDescription() {
		System.out.println("Enter the Description (or just press Enter if no Description needed):");
		return scanner.nextLine();
	}
	
	private static LocalDate readInDate() {
		System.out.println("Enter the Due Date (or just press Enter if no Due Date needed):");
		System.out.println("Please use date format " + DUE_DATE_PATTERN);
		
		String dueDateString = scanner.nextLine();
		LocalDate dueDate = null;
		
		if(!dueDateString.isEmpty()) {
			do {
				try {
					dueDate = LocalDate.parse(dueDateString, dueDateFormat.withResolverStyle(ResolverStyle.STRICT));
					
					if(dueDate.isBefore(LocalDate.now())) {
						System.out.println("Due date has already passed! Please enter a valid date:");
						dueDateString = scanner.nextLine();
						dueDate = null;
					}
				} catch(DateTimeParseException e) {
					System.out.println("Please use date format " + DUE_DATE_PATTERN);
					dueDateString = scanner.nextLine();
				}
			} while(dueDate == null && !dueDateString.isEmpty());
		}
		
		return dueDate;
	}
	
	private static Status readInStatus() {
		String statusId = "";
		Status status = null;
		
		do {
			System.out.println("\nSelect the proper status:");
			for(Status s : Status.values()) {
				System.out.println(s.getId() + ": " + s.getName());
			}
			statusId = scanner.nextLine();
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
	private static boolean confirmAction(String action, List<Task> tasks) {
		System.out.println("\nDo you want to " + action + " task" + (pluralizedAction(action) ? "s" : "") + "?. Enter y for Yes or n for no.");
		printTasks(tasks);
			
		String input = scanner.nextLine();
		
		while(!input.equals(YES) && !input.equals(NO)) {
			System.out.println("Invalid input.  Please enter y to " + action + " or n to discard it.");
			input = scanner.nextLine();
		}
		
		return input.equals(YES);
	}
	
	/**
	 * Confirm action from the user to determine if changes should be kept or discarded
	 * 
	 * @param	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @param	task	a Task that the action could be taken on
	 * @return			boolean - true if the user confirms the action or false if the changes are to be discarded
	 */
	private static boolean confirmAction(String action, Task task) {
		return confirmAction(action, Arrays.asList(task));
	}
	
	/**
	 * Confirm action from the user to determine if changes should be kept or discarded
	 * 
	 * @param	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @return			boolean - true if the user confirms the action or false if the changes are to be discarded
	 */
	private static boolean confirmAction(String action) {
		return confirmAction(action, new ArrayList<>());
	}
	
	private static boolean pluralizedAction(String action) {
		return Arrays.asList(Action.SAVE.getShortName(), Action.REMOVE.getShortName()).contains(action);
	}
	
	/**
	 * Checks if the user wants to perform an additional action of the same type or return to the Main Menu
	 * @param 	action	a string indicating which action the user is trying to take (ie, add, update)
	 * @return			boolean - true if the user wants to return to the Main Menu or false to perform an additional action of the same type
	 */
	private static boolean returnToMenu(String action) {
		System.out.println("\nDo you want to " + action + " another task? Enter y for Yes or n to return to the Main Menu");
		
		String input = scanner.nextLine();
		
		while(!input.equals(YES) && !input.equals(NO)) {
			System.out.println("Invalid input.  Please enter y to " + action + " a task or n to return to the Main Menu");
			input = scanner.nextLine();
		}
		
		if(input.equals(NO)) {
			saveReminder();
		}
		
		return input.equals(NO);
	}
	
	/**
	 * Confirms that the user wants to return to the Main Menu
	 * @return			boolean - true if the user wants to return to the Main Menu or false to remain in their current location
	 */
	private static boolean returnToMenu() {
		System.out.println("\nYou are trying to return to the Main Menu. Enter y to Stay Here or n to return to the Main Menu");
		
		String input = scanner.nextLine();
		
		while(!input.equals(YES) && !input.equals(NO)) {
			System.out.println("Invalid input.  Please enter y to stay here or n to return to the Main Menu");
			input = scanner.nextLine();
		}
		
		if(input.equals(NO)) {
			saveReminder();
		}
		
		return input.equals(NO);
	}
	
	private static void saveReminder() {
		System.out.println("Reminder: Tasks are not saved automatically. Please remember to save before exiting the program.\n");
	}
	
	private static boolean hasNoTasks() {
		if(allTasks.isEmpty()) System.out.println("Task List is Empty! Returning to Main Menu!");
			
		return allTasks.isEmpty();
	}
	
	/**
	 * Allows the user to select which tasks they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @param 	allowMultiple	a boolean, if true, that allows the user to select multiple tasks at once
	 * @param 	tasks			a List of Tasks that the action could be taken on
	 * @return					a List of Task Ids that the user wants to perform the action on
	 */
	private static List<Integer> selectTasks(String action, boolean allowMultiple, List<Task> tasks) {
		System.out.println("\nWhich task(s) do you want to " + action);
		if(tasks == null) taskService.printTasks();
		else taskService.printTasks(tasks);
		System.out.println(EXIT + ". Return to Main Menu");
		
		List<Integer> indicies = new ArrayList<>();
		
		do {
			System.out.println("Enter the id of the task you wish to select." + (allowMultiple ? "You may select multiple tasks at once by separating them with a comma." : ""));
			String input = scanner.nextLine();
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
				System.out.println("Invalid format. Please enter a valid id.");
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
	private static List<Integer> selectTasks(String action, List<Task> tasks) {
		return selectTasks(action, true, tasks);
	}
	
	/**
	 * Allows the user to select which tasks they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @return					a List of Task Ids that the user wants to perform the action on
	 */
	private static List<Integer> selectTasks(String action) {
		return selectTasks(action, true, allTasks);
	}
	
	/**
	 * Allows the user to select which task they would like to perform a given action upon
	 * @param 	action			a string indicating which action the user is trying to take (ie, add, update)
	 * @return					the task id of the Task the user wants to perform the action on
	 */
	private static int selectTask(String action) {
		List<Integer> taskIds = selectTasks(action, false, allTasks);
		
		return taskIds.isEmpty() ? -1 : taskIds.getFirst();
	}
	
	private static void printTasks(List<Task> tasks) {
		IntStream.range(0, tasks.size()).forEach(i -> System.out.println((i + 1) + ": " + tasks.get(i)));
	}
	
	private static List<Task> getTasksFromIds(List<Integer> taskIds) {
		return taskIds.stream().map(id -> allTasks.get(id)).collect(Collectors.toList());
	}
}
