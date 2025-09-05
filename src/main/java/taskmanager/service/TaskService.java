package main.java.taskmanager.service;

import java.time.LocalDate;
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
	private List<Task> originalTasks;
	
	public TaskService() {
		this.tasks = TaskRepository.loadTasks();
		this.originalTasks = List.copyOf(tasks);
	}
	
	public List<Task> getAllTasks() {
		return this.tasks;
	}
	
	public List<Task> getAllTasksByStatus(Status status) {
		return this.tasks.stream().filter(task -> status.equals(task.getStatus())).collect(Collectors.toList());
	}
	
	public List<Task> getAllTasksBeforeDate(LocalDate date, List<Task> tasks) {
		return tasks.stream().filter(task -> task.getDueDate().isBefore(date)).collect(Collectors.toList());
	}
	
	/*
	 * Add new tasks in a way that maintains order by Due Date ASC
	 * If there are no tasks currently or the Task does not have a due date, add the task to the list
	 * Otherwise, find the appropriate spot in the list and insert the task there
	 */
	public void addTask(Task task) {
		if(this.tasks.isEmpty() || task.getDueDate() == null) {
			this.tasks.add(task);
		} else {
			int index = Collections.binarySearch(this.tasks, task, Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
			if(index < 0) index = -index - 1;
	
			this.tasks.add(index, task);
		}
	}
	
	/*
	 * Update tasks in a way that maintains order by Due Date ASC
	 * If the due date has not changed, update in place
	 * Otherwise, remove the task and re-add it with the addTask method to place the Task in the proper location in the list
	 */
	public void updateTask(int index, Task task) {
		if(tasks.get(index).getDueDate() != task.getDueDate()) {
			this.tasks.remove(index);
			addTask(task);
		} else this.tasks.set(index, task);
	}
	
	public void removeTasksById(List<Integer> taskIds) {
		taskIds.stream().sorted(Comparator.reverseOrder()).forEach(taskId -> tasks.remove((int) taskId));
	}
	
	public void removeTasks(List<Task> tasks) {
		tasks.stream().forEach(task -> this.tasks.remove(task));
	}
	
	public boolean hasTaskListChanged() {
		return this.tasks.equals(originalTasks);
	}
	
	public void printTasks(List<Task> tasks) {
		if(tasks.isEmpty()) printNoTasks();
		else IntStream.range(0, tasks.size()).forEach(i -> System.out.println((i + 1) + ": " + tasks.get(i)));
	}
	
	public void printTasks() {
		printTasks(this.tasks);
	}
	
	public void printTasksById(List<Integer> taskIds) {
		if(this.tasks.isEmpty()) printNoTasks();
		else taskIds.stream().forEach(i -> System.out.println((i + 1) + ": " + this.tasks.get(taskIds.get(i))));
	}
	
	private void printNoTasks() {
		System.out.println("\nYou currently have no tasks.");
	}
}
