package main.java.taskmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.repository.TaskRepository;
import main.java.taskmanager.util.csv.CSVParser;
import main.java.taskmanager.util.csv.CSVWriter;

public class TaskManager {
	public static void main(String[] args) {
		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String date = formatter.format(new Date());
		Task task = new Task("Eat Lunch", "Make, Take, and Eat Lunch", date);
		System.out.println(task.toString());
		System.out.println(task.getStatus());
		
		Task smallTask = new Task("Change filters", null, null);
		System.out.println(smallTask.toString());
		System.out.println(smallTask.getStatus());
		
		List<Task> tasks = new ArrayList<>();
		tasks.add(task);
		tasks.add(smallTask);
		
		TaskRepository.saveTasks(tasks);
		
		List<Task> loadedTasks = TaskRepository.loadTasks();
		
		for(Task t : loadedTasks) {
			if(t != null) {
				System.out.println(t.toString());
			}
		}
	}
}
