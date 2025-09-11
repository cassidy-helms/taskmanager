package taskmanager.service;

import java.time.LocalDate;
import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;

public class TaskServiceFindTasksOnOrBeforeDateTest {
    protected static void findTasksOnOrBeforeDateTests() {
    	testFindTasksOnOrBeforeDateWhenPresentReturnsMatchingTasks();
    	testFindTasksOnOrBeforeDateWhenNotPresentReturnsEmptyList();
    	testFindIncompleteTasksWhenTaskListEmptyReturnsEmptyList();
    }
    
    private static void testFindTasksOnOrBeforeDateWhenPresentReturnsMatchingTasks() {
        Task previousTask1 = TaskServiceTest.setupBasicTask(LocalDate.now().minusDays(1));
        Task previousTask2 = TaskServiceTest.setupBasicTask(LocalDate.now());
        Task futureTask = TaskServiceTest.setupBasicTask(LocalDate.now().plusDays(1));
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(previousTask1, previousTask2, futureTask));
        TaskService service = new TaskService(repo);

        TaskServiceTest.assertTrue(service.findTasksOnOrBeforeDate(LocalDate.now(), service.getAllTasks()).equals(Arrays.asList(previousTask1, previousTask2)), "Find Tasks On or Before Date - When Present Returns Matching Tasks");
    }
    
    private static void testFindTasksOnOrBeforeDateWhenNotPresentReturnsEmptyList() {
        Task futureTask1 = TaskServiceTest.setupBasicTask(LocalDate.now().plusDays(2));
        Task futureTask2 = TaskServiceTest.setupBasicTask(LocalDate.now().plusDays(1));
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(futureTask1, futureTask2));
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findTasksOnOrBeforeDate(LocalDate.now(), service.getAllTasks()).isEmpty(), "Find Tasks On or Before Date - When NOT Present Returns Empty List");
    }
    
    private static void testFindIncompleteTasksWhenTaskListEmptyReturnsEmptyList() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findTasksOnOrBeforeDate(LocalDate.now(), service.getAllTasks()).isEmpty(), "Find Tasks On or Before Date - When Task List Empty Returns Empty List");
    }
}
