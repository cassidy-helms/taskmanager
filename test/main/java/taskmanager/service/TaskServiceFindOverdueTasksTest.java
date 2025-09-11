package taskmanager.service;

import java.time.LocalDate;
import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;

public class TaskServiceFindOverdueTasksTest {
    protected static void findOverdueTasksTests() {
    	testOverdueTasksWhenPresentReturnsMatchingTasks();
    	testOverdueTasksWhenNotPresentReturnsEmptyList();
    	testOverdueTasksWhenTaskListEmptyReturnsEmptyList();
    }
    
    private static void testOverdueTasksWhenPresentReturnsMatchingTasks() {
        Task overdueTask = TaskServiceTest.setupBasicTask(LocalDate.now().minusDays(1));
        Task onTimeTask = TaskServiceTest.setupBasicTask(LocalDate.now());
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(overdueTask, onTimeTask));
        TaskService service = new TaskService(repo);

        TaskServiceTest.assertTrue(service.findOverdueTasks().equals(Arrays.asList(overdueTask)), "Find Overdue Tasks - When Present Returns Matching Tasks");
    }
    
    private static void testOverdueTasksWhenNotPresentReturnsEmptyList() {
        Task onTimeTask1 = TaskServiceTest.setupBasicTask(LocalDate.now());
        Task onTimeTask2 = TaskServiceTest.setupBasicTask(LocalDate.now().plusDays(1));
        
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(onTimeTask1, onTimeTask2));
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findOverdueTasks().isEmpty(), "Find Overdue Tasks - When NOT Present Returns Empty List");
    }
    
    private static void testOverdueTasksWhenTaskListEmptyReturnsEmptyList() {
        TaskRepository repo = new MockTaskRepository();
        TaskService service = new TaskService(repo);
        
        TaskServiceTest.assertTrue(service.findOverdueTasks().isEmpty(), "Find Overdue Tasks - When Task List Empty Returns Empty List");
    }
}
