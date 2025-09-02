package main.java.taskmanager.service;

import java.util.List;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.repository.TaskRepository;

public class TaskService {
	private List<Task> tasks;
	
	public TaskService() {
		this.tasks = TaskRepository.loadTasks();
	}
	
	public List<Task> getAllTasks() {
		return this.tasks;
	}
	
	public void addTask(Task task) {
		this.tasks.add(task);
	}
	
	public void updateTask(int location, Task task) {
		tasks.set(location, task);
	}
	
	public void removeTask(int location) {
		tasks.remove(location);
	}
}
