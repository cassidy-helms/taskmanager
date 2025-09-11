package taskmanager.service;

import java.time.LocalDate;
import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;
import taskmanager.util.enums.Status;

public class TaskServiceAddTaskTest {
    protected static void addTaskTests() {
    	testAddTaskWhenTaskListEmptyTaskIsAdded();
    	testAddTaskWhenTaskDueDateIsNullTaskIsAddToBackOfList();
    	testAddTaskWhenMultipleTasksAddedOrdersTasksByDateAsc();
    	testAddTaskWhenWithNullDescriptionAndDueDatePreservesNullValues();
    }

    private static void testAddTaskWhenTaskListEmptyTaskIsAdded() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
        service.addTask(TaskServiceTest.setupBasicTask());
        
        TaskServiceTest.assertTrue(service.getAllTasks().size() == 1, "Add Task - When Task List Empty Task is Added");
    }
    
    private static void testAddTaskWhenTaskDueDateIsNullTaskIsAddToBackOfList() {
    	Task initialTask = TaskServiceTest.setupBasicTask(LocalDate.now());
    	TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(initialTask));
    	TaskService service = new TaskService(repo);
    	
    	Task taskToAdd = TaskServiceTest.setupBasicTask(null, null, null, null);
    	service.addTask(taskToAdd);
    	
    	TaskServiceTest.assertTrue(service.getAllTasks().equals(Arrays.asList(initialTask, taskToAdd)), "Add Task - When Task Due Date is Null Task is Added to Back of List");
    }

    private static void testAddTaskWhenMultipleTasksAddedOrdersTasksByDateAsc() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
        Task task1 = new Task("Task 1", "Description 1", LocalDate.now().plusDays(2), Status.TODO);
        service.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", null, Status.IN_PROGRESS);  // Null Date Should Go Last
        service.addTask(task2);
        Task task3 = new Task("Task 3", "Description 3", LocalDate.now().plusDays(3), Status.COMPLETED);
        service.addTask(task3);
        Task task4 = new Task("Task 4", "Description 4", LocalDate.now(), Status.TODO);
        service.addTask(task4);
        
        TaskServiceTest.assertTrue(service.getAllTasks().equals(Arrays.asList(task4, task1, task3, task2)), "Add Task - When Multiple Tasks Added Orders Tasks in Due Date ASC Order");
    }
    
    private static void testAddTaskWhenWithNullDescriptionAndDueDatePreservesNullValues() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
    	Task task = TaskServiceTest.setupBasicTask("Test Task", null, null, Status.TODO);
    	service.addTask(task);
    	
    	TaskServiceTest.assertTrue(service.getAllTasks().getFirst().equals(task), "Add Task - When Task Added Contains Null Description and Null Due Date, Preserves Null Values");
    }
}
