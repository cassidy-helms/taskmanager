package taskmanager.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;

public class TaskServiceRemoveTaskByIdTest {
	protected static void removeTasksByIdTests() {
		testRemoveTasksByIdWhenOutOfOrderCorrectlyRemovesTasks();
		testRemoveTasksByIdCanRemoveLastTask();
	}

    private static void testRemoveTasksByIdWhenOutOfOrderCorrectlyRemovesTasks() {
        Task task1 = TaskServiceTest.setupBasicTask();
        Task task2 = TaskServiceTest.setupBasicTask();
        Task task3 = TaskServiceTest.setupBasicTask();
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(task1, task2, task3));
        TaskService service = new TaskService(repo);
        
        service.removeTasksById(Arrays.asList(2, 0));
        TaskServiceTest.assertTrue(service.getAllTasks().equals(Arrays.asList(task2)), "Remove Task By Id - When Task Ids Out of Order, Correctly Removes Tasks");
    }
    
    private static void testRemoveTasksByIdCanRemoveLastTask() {
    	Task task = TaskServiceTest.setupBasicTask(LocalDate.now());

        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(task));
        TaskService service = new TaskService(repo);
        
        service.removeTasksById(Arrays.asList(0));
        TaskServiceTest.assertTrue(service.getAllTasks().equals(new ArrayList<>()), "Remove Task By Id - Can Remove Last Task");
    }
}
