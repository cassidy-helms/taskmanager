package main.java.taskmanager.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.repository.TaskRepository;
import main.java.taskmanager.util.enums.Status;

public class TaskService {
	private final List<Task> tasks;
	private final TaskRepository taskRepository;
	private List<Task> originalTasks;
	private Map<Status, Long> statusCount;
	private long overdueTaskCount;
	
	public TaskService() {
		this.taskRepository = new TaskRepository();
		this.tasks = taskRepository.loadTasks();
		this.originalTasks = List.copyOf(this.tasks);
		this.statusCount = compileTaskCountByStatus();
		this.overdueTaskCount = computeOverdueTaskCount();
	}
	
	/**
	 * Gets all tasks, including unsaved changes
	 * @return	List of Tasks
	 */
	public List<Task> getAllTasks() {
		return Collections.unmodifiableList(this.tasks);
	}
	
	/**
	 * Gets all tasks with a given status
	 * @param 	status	Status of the task (ie, To Do)
	 * @return			List of Tasks with a given status
	 */
	public List<Task> findTasksByStatus(Status status) {
		Objects.requireNonNull(status, "Status must not be null");
		return this.tasks.stream().filter(task -> status.equals(task.getStatus())).collect(Collectors.toList());
	}

	/**
	 * Gets all tasks in with an incomplete status (ie, To Do or In Progress)
	 * @return	List of Tasks that do not have a status of Complete
	 */
	public List<Task> findIncompleteTasks() {
		return this.tasks.stream().filter(task -> !task.getStatus().equals(Status.COMPLETED)).collect(Collectors.toList());
	}
	
	/**
	 * Filters given task list by due date on or before the given date
	 * @param date				date to filter tasks on
	 * @param tasksToFilter		List of Tasks to filter on
	 * @return					List of Tasks with a due date on or before the given date
	 */
	public List<Task> findTasksOnOrBeforeDate(LocalDate date, List<Task> tasksToFilter) {
		Objects.requireNonNull(date, "Date must not be null");
		Objects.requireNonNull(tasksToFilter, "Tasks To Filter must not be null");
		return tasksToFilter.stream().filter(task -> task.getDueDate() != null && (task.getDueDate().isBefore(date) || task.getDueDate().isEqual(date))).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves the number of tasks that are overdue (due date is before the current date)
	 * @return	count of overdue tasks
	 */
	public Long getTaskCountOverdue() {
		return this.overdueTaskCount;
	}
	
	/**
	 * Retrieves the number of tasks that are in a given status
	 * @param status	Status of that tasks to count
	 * @return			count of tasks in the given status
	 */
	public Long getTaskCountByStatus(Status status) {
		Objects.requireNonNull(status, "Status must not be null");
		return this.statusCount.getOrDefault(status,(long) 0);
	}
	
	/**
	 * Add new tasks in a way that maintains order by Due Date ASC
	 * If there are no tasks currently or the Task does not have a due date, add the task to the list
	 * Otherwise, find the appropriate spot in the list and insert the task there
	 * 
	 * @param task	Task to be added
	 */
	public void addTask(Task task) {
		Objects.requireNonNull(task, "Task must not be null");
		if(this.tasks.isEmpty() || task.getDueDate() == null) {
			this.tasks.add(task);
		} else {
			int index = Collections.binarySearch(this.tasks, task, Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
			if(index < 0) index = -index - 1;
	
			this.tasks.add(index, task);
		}
		addToStatusCount(task.getStatus());
	}
	
	/**
	 * Update tasks in a way that maintains order by Due Date ASC
	 * If the due date has not changed, update in place
	 * Otherwise, remove the task and re-add it with the addTask method to place the Task in the proper location in the list
	 * 
	 * @param index		index of the task to update
	 * @param task		Task with updated values
	 */
	public void updateTask(int index, Task task) {
		Objects.requireNonNull(index, "Index must not be null");
		Objects.requireNonNull(task, "Task must not be null");
		
		Task oldTask = this.tasks.get(index);
		if(oldTask.getStatus() != task.getStatus()) {
			if(oldTask.isOverdue() && task.getStatus() == Status.COMPLETED) --this.overdueTaskCount;
			else if(oldTask.getStatus() == Status.COMPLETED && task.isOverdue()) ++this.overdueTaskCount;
			
			removeFromStatusCount(oldTask.getStatus());
			addToStatusCount(task.getStatus());
		}
		
		if(!Objects.equals(oldTask.getDueDate(), task.getDueDate())) {
			this.tasks.remove(index);
			addTask(task);
		} else this.tasks.set(index, task);
	}
	
	/**
	 * Remove tasks by ids
	 * @param taskIds	List of ids of Tasks to remove
	 */
	public void removeTasksById(List<Integer> taskIds) {
		Objects.requireNonNull(taskIds, "Task IDs must not be null");
		taskIds.stream().sorted(Comparator.reverseOrder()).forEach(taskId -> {
			Task taskToRemove = this.tasks.get(taskId);
			if(taskToRemove.isOverdue()) --this.overdueTaskCount;
			removeFromStatusCount(taskToRemove.getStatus());
			this.tasks.remove((int) taskId);
		});
	}
	
	/**
	 * Remove tasks
	 * @param tasksToRemove		List of Tasks to remove
	 */
	public void removeTasks(List<Task> tasksToRemove) {
		Objects.requireNonNull(tasksToRemove, "Tasks must not be null");
		tasksToRemove.stream().forEach(task -> {
			if(task.isOverdue()) --this.overdueTaskCount;
			removeFromStatusCount(task.getStatus());
		});
		this.tasks.removeAll(tasksToRemove);
	}
	
	/**
	 * Saves tasks to the CSV File
	 */
	public void saveTasks() {
		taskRepository.saveTasks(this.tasks);
		this.originalTasks = List.copyOf(this.tasks);
	}
	
	/**
	 * Checks if tasks have been updated since load
	 * @return	boolean - true if the task list has changed or false if it has not
	 */
	public boolean hasTaskListChanged() {
		return !this.tasks.equals(originalTasks);
	}
	
	private Map<Status, Long> compileTaskCountByStatus() {
		return this.tasks.stream().collect(Collectors.groupingBy(task -> task.getStatus(), Collectors.counting()));
	}
	
	private void addToStatusCount(Status status) {
		Long currentCount = this.statusCount.getOrDefault(status,(long) 0);
		this.statusCount.put(status, ++currentCount);
	}
	
	private void removeFromStatusCount(Status status) {
		Long currentCount = this.statusCount.getOrDefault(status,(long) 0);
		this.statusCount.put(status, --currentCount);
	}
	
	private long computeOverdueTaskCount() {
		return this.tasks.stream().filter(task -> task.isOverdue()).count();
	}
}
