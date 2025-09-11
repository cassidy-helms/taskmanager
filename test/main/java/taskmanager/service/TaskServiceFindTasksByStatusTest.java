package taskmanager.service;

import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;
import taskmanager.util.enums.Status;

public class TaskServiceFindTasksByStatusTest {
    protected static void findTasksByStatusTests() {
    	testFindTasksByStatusWhenPresentReturnsMatchingTasks();
    	testFindTasksByStatusWhenNotPresentReturnsEmptyList();
    	testFindTasksByStatusWhenTaskListEmptyReturnsEmptyList();
    }
    
    private static void testFindTasksByStatusWhenPresentReturnsMatchingTasks() {
        Task toDoTask = TaskServiceTest.setupBasicTask(Status.TODO);
        Task completedTask = TaskServiceTest.setupBasicTask(Status.COMPLETED);
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(toDoTask, completedTask));
        TaskService service = new TaskService(repo);

        TaskServiceTest.assertTrue(service.findTasksByStatus(Status.TODO).equals(Arrays.asList(toDoTask)), "Find Tasks By Status - When Present Returns Matching Tasks");
    }
    
    private static void testFindTasksByStatusWhenNotPresentReturnsEmptyList() {
        Task inProgressTask = TaskServiceTest.setupBasicTask(Status.IN_PROGRESS);
        Task completedTask = TaskServiceTest.setupBasicTask(Status.COMPLETED);
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(inProgressTask, completedTask));
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findTasksByStatus(Status.TODO).isEmpty(), "Find Tasks By Status - When NOT Present Returns Empty List");
    }
    
    private static void testFindTasksByStatusWhenTaskListEmptyReturnsEmptyList() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findTasksByStatus(Status.TODO).isEmpty(), "Find Tasks By Status - When Task List Empty Returns Empty List");
    }
}
