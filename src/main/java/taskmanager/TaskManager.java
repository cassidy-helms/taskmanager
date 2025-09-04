package main.java.taskmanager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.repository.TaskRepository;
import main.java.taskmanager.service.TaskService;
import main.java.taskmanager.util.enums.Action;
import main.java.taskmanager.util.enums.Status;


public class TaskManager {	
	private static final DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final String DUE_DATE_PATTERN = "yyyy-MM-dd";
	private static final TaskService taskService;
	private static final Scanner scanner;
	private static List<Task> allTasks;
	
	static {
		taskService = new TaskService();
		scanner = new Scanner(System.in);
		allTasks = taskService.getAllTasks();
	}
	
	public static void main(String[] args) {		
		System.out.println("Welcome to the Task Manager.\n");
		
		String input = "";
		
		do {
			System.out.println("Select which option you would like to do:");
			for(int i = 0; i < Action.values().length; i++) {
				System.out.println((i + 1) + ": " + Action.values()[i].getLongName());
			}
			
			System.out.print("Option: ");
			
			input = scanner.nextLine();
			Action action = Action.lookupActionById(input);
			
			switch(action) {
				case Action.VIEW:
					taskService.printTasks();
					break;
				case Action.ADD:
					addTask();
					break;
				case Action.MARK_COMPLETE:
					markTaskComplete();
					break;
				case Action.UPDATE:
					// Update Task
					break;
				case Action.REMOVE:
					// Remove Task
					break;
				case Action.SAVE:
					saveTasks();
					break;
				case Action.EXIT:
					// Exit
					break;
				default:
					System.out.println("Invalid input.  Please try again.");
			}
			
		} while(!input.equals("7"));
		
		scanner.close();
	}
	
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
		} else {
			System.out.println("Discarding Task...");
		}
		
		if(!returnToMenu(Action.ADD.getShortName())) addTask();
	}
	
	private static void markTaskComplete() {
		System.out.println("Which task(s) do you want to complete?");
		List<Integer> taskIds = selectTasks();
		List<Task> tasks = getTasksFromIds(taskIds);
		
		if(confirmAction(Action.MARK_COMPLETE.getShortName(), tasks)) {
			for(int id : taskIds) {
				Task taskToComplete = allTasks.get(id);
				taskToComplete.setStatus(Status.COMPLETED);
				allTasks.set(id, taskToComplete);
			}
		}
		
		System.out.println("Tasks marked as complete!");
		
		if(!returnToMenu(Action.MARK_COMPLETE.getShortName())) markTaskComplete();
	}
	
	private static void saveTasks() {
		if(confirmAction(Action.SAVE.getShortName())) {
			System.out.println("Saving tasks...");
			TaskRepository.saveTasks(allTasks);
			System.out.println("Tasks saved!");
		}
	}
	
	private static String readInTitle() {
		System.out.println("Enter the Title: ");
		return scanner.nextLine();
	}
	
	private static String readInDescription() {
		System.out.println("Enter the Description (or just press Enter if no Description needed):");
		return scanner.nextLine();
	}
	
	// TODO: currently allows past dates
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
			System.out.println("Select the proper status:");
			for(Status s : Status.values()) {
				System.out.println(s.getId() + ": " + s.getName());
			}
			statusId = scanner.nextLine();
			status = Status.lookupStatusById(statusId);
		} while (status == null);
		
		return status;
	}
	
	private static boolean confirmAction(String action, List<Task> tasks) {
		System.out.println("\nDo you want to " + action + " task" + (pluralizedAction(action) ? "s" : "") + "?. Enter y for Yes or n for no.");
		printTasks(tasks);
			
		String input = scanner.nextLine();
		
		while(!input.equals("y") && !input.equals("n")) {
			System.out.println("Invalid input.  Please enter y to " + action + " or n to discard it.");
			input = scanner.nextLine();
		}
		
		return input.equals("y");
	}
	
	private static boolean confirmAction(String action, Task task) {
		return confirmAction(action, Arrays.asList(task));
	}
	
	private static boolean confirmAction(String action) {
		return confirmAction(action, new ArrayList<>());
	}
	
	private static boolean pluralizedAction(String action) {
		return Arrays.asList(Action.SAVE.getShortName()).contains(action);
	}
	
	private static boolean returnToMenu(String action) {
		System.out.println("\nDo you want to " + action + " another task? Enter y for Yes or n to return to the Main Menu");
		String input = scanner.nextLine();
		
		while(!input.equals("y") && !input.equals("n")) {
			System.out.println("Invalid input.  Please enter y to " + action + " a task or n to return to the Main Menu");
			input = scanner.nextLine();
		}
		
		if(input.equals("n")) System.out.println("Reminder: Tasks are not saved automatically. Please remember to save before exiting the program.\n");
		
		return input.equals("n");
	}
	
	private static List<Integer> selectTasks(boolean allowMultiple) {
		System.out.println("Current Tasks:");
		taskService.printTasks();
		
		List<Integer> indicies = new ArrayList<>();
		
		do {
			System.out.println("Enter the id of the task you wish to select." + (allowMultiple ? "You may select multiple tasks at once by separating them with a comma." : ""));
			String input = scanner.nextLine();
			String[] splitInput = Arrays.stream(input.split(","))
					  .map(String::trim)
					  .toArray(String[]::new);
			
			try {
				for(String s : splitInput) {
					int index = Integer.parseInt(s) - 1;
					
					if(index < 0 || index > allTasks.size()) throw new Exception("Invalid Task Id");
					else indicies.add(index);
				}
			} catch (Exception e) {
				System.out.println("Invalid format. Please enter a valid id.");
				indicies.clear();
			}
		} while(indicies.isEmpty());
		
		return indicies;
	}
	
	private static List<Integer> selectTasks() {
		return selectTasks(true);
	}
	
	private static int selectTask() {
		return selectTasks(false).getFirst();
	}
	
	private static void printTasks(List<Task> tasks) {
		IntStream.range(0, tasks.size()).forEach(i -> System.out.println((i + 1) + ": " + tasks.get(i)));
	}
	
	private static List<Task> getTasksFromIds(List<Integer> taskIds) {
		List<Task> tasks = new ArrayList<>();
		
		for(int id : taskIds) {
			tasks.add(allTasks.get(id));
		}
		
		return tasks;
	}
}
