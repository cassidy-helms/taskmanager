package main.java.taskmanager.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.repository.TaskRepository;
import main.java.taskmanager.util.enums.Status;

public class TaskService {
	private List<Task> tasks;
	
	public TaskService() {
		this.tasks = TaskRepository.loadTasks();
	}
	
	public List<Task> getAllTasks() {
		return List.copyOf(this.tasks);
	}
	
	public List<Task> getAllTasksByStatus(Status status) {
		return this.tasks.stream().filter(task -> status.equals(task.getStatus())).collect(Collectors.toList());
	}
	
	public void addTask(Task task) {
		if(this.tasks.size() <= 1 || task.getDueDate() == null) {
			this.tasks.add(task);
		} else {
			int index = Collections.binarySearch(this.tasks, task, Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
			if(index < 0) {
				index = -index - 1;
			}
			this.tasks.add(index, task);
		}
	}
	
	public void updateTask(int index, Task task) {
		tasks.set(index, task);
	}
	
	public void removeTask(int index) {
		tasks.remove(index);
	}
	
	public void printTasks(List<Task> tasks) {
		if(tasks.isEmpty()) {
			System.out.println("You currently have no tasks.");
		} else {
			IntStream.range(0, tasks.size()).forEach(i -> System.out.println((i + 1) + ": " + tasks.get(i)));
		}
	}
	
	public void printTasks() {
		printTasks(this.tasks);
	}
	
	public void printTasksById(List<Integer> taskIds) {
		if(this.tasks.isEmpty()) {
			System.out.println("You currently have no tasks.");
		} else {
			for(int i = 0; i < taskIds.size(); i++) {
				System.out.println((i + 1) + ": " + this.tasks.get(taskIds.get(i)));
			}
		}
	}
}
