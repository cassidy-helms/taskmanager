package main.java.taskmanager.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

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
	
	public void printTasks() {
		if(this.tasks.isEmpty()) {
			System.out.println("You currently have no tasks.");
		} else {
			IntStream.range(0, this.tasks.size()).forEach(i -> System.out.println((i + 1) + ": " + this.tasks.get(i)));
		}
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
