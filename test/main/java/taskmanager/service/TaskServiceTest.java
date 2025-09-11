package taskmanager.service;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.util.enums.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskServiceTest {
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	
    public static void main(String[] args) {
    	TaskServiceAddTaskTest.addTaskTests();
    	TaskServiceFindTasksByStatusTest.findTasksByStatusTests();
    	TaskServiceFindIncompleteTasksTest.findIncompleteTasksTests();
    	TaskServiceFindOverdueTasksTest.findOverdueTasksTests();
    	TaskServiceFindTasksOnOrBeforeDateTest.findTasksOnOrBeforeDateTests();
        TaskServiceUpdateTaskTest.updateTasksTests();
        TaskServiceRemoveTaskByIdTest.removeTasksByIdTests();
        TaskServiceRemoveTaskTest.removeTasksTests();
        System.out.println("All tests completed.");
    }

    protected static void assertTrue(boolean condition, String testName) {
        if(condition) {
            System.out.println("PASSED: " + testName);
        }else {
            System.out.println(ANSI_RED + "FAILED: " + testName + ANSI_RESET);
        }
    }
    
    protected static Task setupBasicTask(String title, String description, LocalDate dueDate, Status status) {
    	title = title == null ? "Test Task" : title;
    	description = description == null ? "Test Description" : description;
    	dueDate = dueDate == null ? null : dueDate;
    	status = status == null ? Status.TODO : status;
    	
    	Task task = new Task(title, description, dueDate, status);
    	
    	return task;
    }
    
    protected static Task setupBasicTask() {
    	return setupBasicTask(null, null, null, null);
    }
    
    protected static Task setupBasicTask(LocalDate dueDate) {
    	return setupBasicTask(null, null, dueDate, null);
    }
    
    protected static Task setupBasicTask(Status status) {
    	return setupBasicTask(null, null, null, status);
    }

    static class MockTaskRepository extends TaskRepository {
        private List<Task> tasks = new ArrayList<>();
        @Override
        public List<Task> loadTasks() {
            return tasks;
        }
        @Override
        public void saveTasks(List<Task> tasks) {
            this.tasks = new ArrayList<>(tasks);
        }
    }
    
    static class MockTaskRepositoryWithData extends MockTaskRepository {
        private List<Task> tasks = new ArrayList<>();

    	public MockTaskRepositoryWithData(List<Task> tasks) {
    		this.tasks = tasks;
    	}
    	
        @Override
        public List<Task> loadTasks() {
            if(this.tasks.isEmpty()) return setupTaskList();
            return new ArrayList<>(this.tasks);
        }
        
        private List<Task> setupTaskList() {
            Task task1 = new Task("Task 1", "Description 1", LocalDate.now().plusDays(2), Status.TODO);
            Task task2 = new Task("Task 2", "Description 2", null, Status.IN_PROGRESS);
            Task task3 = new Task("Task 3", "Description 3", LocalDate.now().plusDays(3), Status.COMPLETED);
            Task task4 = new Task("Task 4", "Description 4", LocalDate.now(), Status.TODO);
            
            return Arrays.asList(task4, task1, task3, task2);
        }
    }
}
