package taskmanager.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;
import taskmanager.util.enums.Status;

public class TaskServiceRemoveTaskTest {
	protected static void removeTasksTests() {
		testRemoveTasksWhenOutOfOrderCorrectlyRemovesTasks();
		testRemoveTasksCanRemoveLastTask();
	}

    private static void testRemoveTasksWhenOutOfOrderCorrectlyRemovesTasks() {
        Task task1 = TaskServiceTest.setupBasicTask(Status.TODO);
        Task task2 = TaskServiceTest.setupBasicTask(Status.IN_PROGRESS);
        Task task3 = TaskServiceTest.setupBasicTask(Status.COMPLETED);
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(task1, task2, task3));
        TaskService service = new TaskService(repo);
        
        service.removeTasks(Arrays.asList(task1, task3));
        TaskServiceTest.assertTrue(service.getAllTasks().equals(Arrays.asList(task2)), "Remove Task - When Task Ids Out of Order, Correctly Removes Tasks");
    }
    
    private static void testRemoveTasksCanRemoveLastTask() {
    	Task task = TaskServiceTest.setupBasicTask(LocalDate.now());

        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(task));
        TaskService service = new TaskService(repo);
        
        service.removeTasks(Arrays.asList(task));
        TaskServiceTest.assertTrue(service.getAllTasks().equals(new ArrayList<>()), "Remove Task - Can Remove Last Task");
    }
}
