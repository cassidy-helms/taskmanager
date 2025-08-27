package main.java.taskmanager;

import java.util.Date;

import main.java.taskmanager.model.Task;

public class TaskManager {
	public static void main(String[] args) {
		Task task = new Task("Eat Lunch", "Make and Eat Lunch", new Date());
		System.out.println(task.toString());
		System.out.println(task.getStatus());
	}
}
