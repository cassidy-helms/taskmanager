package main.java.taskmanager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.service.TaskService;
import main.java.taskmanager.util.enums.Status;


public class TaskManager {	
	private static final DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final String DUE_DATE_PATTERN = "yyyy-MM-dd";
	private static final TaskService taskService;
	private static final Scanner scanner;

	private static final String[] options = { 
			"View Tasks",
			"Add Task",
			"Mark Task as Complete",
			"Update Task",
			"Remove Task",
			"Save Changes",
			"Exit"
	};
	
	static {
		taskService = new TaskService();
		scanner = new Scanner(System.in);
	}
	
	public static void main(String[] args) {		
		System.out.println("Welcome to the Task Manager.\n");
		
		String input = "";
		
		do {
			System.out.println("Select which option you would like to do:");
			for(int i = 0; i < options.length; i++) {
				System.out.println((i + 1) + ": " + options[i]);
			}
			
			System.out.print("Option: ");
			
			input = scanner.nextLine();
			
			switch(input) {
				case "1":
					taskService.printTasks();
					break;
				case "2":
					addTask();
					break;
				case "3":
					// Mark Task As Complete
					break;
				case "4":
					// Update Task
					break;
				case "5":
					// Remove Task
					break;
				case "6":
					// Save Changes
					break;
				case "7":
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
		
		System.out.println("Enter the Title: ");
		String title = scanner.nextLine();
		
		System.out.println("Enter the Description (or just press Enter if no Description needed):");
		String description = scanner.nextLine();
		
		System.out.println("Enter the Due Date (or just press Enter if no Due Date needed):");
		System.out.println("Please use date format " + DUE_DATE_PATTERN);
		
		String dueDateString = scanner.nextLine();
		LocalDate dueDate = null;
		
		if(!dueDateString.isEmpty()) {
			do {
				try {
					dueDate = LocalDate.parse(dueDateString, dueDateFormat.withResolverStyle(ResolverStyle.STRICT));
				} catch(DateTimeParseException e) {
					System.out.println("Please use date format " + DUE_DATE_PATTERN);
					dueDateString = scanner.nextLine();
				}
			} while(dueDate == null && !dueDateString.isEmpty());
		}
		
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
		
		Task task = new Task(title, description, dueDate, status);
		System.out.println("Do you want to add this task?. Enter y for Yes or n for no.");
		System.out.println(task.toString());
		String input = scanner.nextLine();
		
		while(!input.equals("y") && !input.equals("n")) {
			System.out.println("Invalid input.  Please enter y to add the task or n to discard it.");
			input = scanner.nextLine();
		}
		
		if(input.equals("y")) {
			System.out.println("Adding Task...");
			taskService.addTask(task);
		} else {
			System.out.println("Discarding Task...");
		}
		
		System.out.println("Do you want to add another task? Enter y for Yes or n to return to the Main Menu");
		input = scanner.nextLine();
		
		while(!input.equals("y") && !input.equals("n")) {
			System.out.println("Invalid input.  Please enter y to add a new task or n to return to the Main Menu");
			input = scanner.nextLine();
		}
		
		if(input.equals("y")) addTask();
		else System.out.println("Reminder: Tasks are not saved automatically. Please remember to save before exiting the program.\n");
	}
}
