package taskmanager.service;

import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;
import taskmanager.util.enums.Status;

public class TaskServiceFindIncompleteTasksTest {
    protected static void findIncompleteTasksTests() {
    	testFindIncompleteTasksWhenPresentReturnsMatchingTasks();
    	testFindIncompleteTasksWhenNotPresentReturnsEmptyList();
    	testFindIncompleteTasksWhenTaskListEmptyReturnsEmptyList();
    }
    
    private static void testFindIncompleteTasksWhenPresentReturnsMatchingTasks() {
        Task toDoTask = TaskServiceTest.setupBasicTask(Status.TODO);
        Task inProgressTask = TaskServiceTest.setupBasicTask(Status.IN_PROGRESS);
        Task completedTask = TaskServiceTest.setupBasicTask(Status.COMPLETED);
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(toDoTask, inProgressTask, completedTask));
        TaskService service = new TaskService(repo);

        TaskServiceTest.assertTrue(service.findIncompleteTasks().equals(Arrays.asList(toDoTask, inProgressTask)), "Find Incomplete Tasks - When Present Returns Matching Tasks");
    }
    
    private static void testFindIncompleteTasksWhenNotPresentReturnsEmptyList() {
        Task completedTask1 = TaskServiceTest.setupBasicTask(Status.COMPLETED);
        Task completedTask2 = TaskServiceTest.setupBasicTask(Status.COMPLETED);
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(completedTask1, completedTask2));
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findIncompleteTasks().isEmpty(), "Find Incomplete Tasks - When NOT Present Returns Empty List");
    }
    
    private static void testFindIncompleteTasksWhenTaskListEmptyReturnsEmptyList() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findIncompleteTasks().isEmpty(), "Find Incomplete Tasks - When Task List Empty Returns Empty List");
    }
}
