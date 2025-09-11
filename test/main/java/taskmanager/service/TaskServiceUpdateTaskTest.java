package taskmanager.service;

import java.time.LocalDate;
import java.util.Arrays;

import taskmanager.model.Task;
import taskmanager.repository.TaskRepository;
import taskmanager.service.TaskServiceTest.MockTaskRepositoryWithData;
import taskmanager.util.enums.Status;

public class TaskServiceUpdateTaskTest {
	protected static void updateTasksTests() {
		testUpdateTaskWhenTaskUpdatedAllFieldsUpdated();
		testUpdateTaskWhenTaskDueDateUpdatedTaskListReordered();
	}

    private static void testUpdateTaskWhenTaskUpdatedAllFieldsUpdated() {
        Task task = TaskServiceTest.setupBasicTask();
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(task));
        TaskService service = new TaskService(repo);
        
        Task updatedTask = new Task("Updated Title", "Updated Description", LocalDate.now(), Status.COMPLETED);
        service.updateTask(0, updatedTask);
        TaskServiceTest.assertTrue(service.getAllTasks().getFirst().equals(updatedTask), "Update Task - When Task Updated All Fields Updated");
    }
    
    private static void testUpdateTaskWhenTaskDueDateUpdatedTaskListReordered() {
    	Task task1 = TaskServiceTest.setupBasicTask(LocalDate.now());
    	Task task2 = TaskServiceTest.setupBasicTask(LocalDate.now().plusDays(1));
    	Task task3 = TaskServiceTest.setupBasicTask(LocalDate.now().plusDays(2));
    	
        TaskRepository repo = new MockTaskRepositoryWithData(Arrays.asList(task1, task2, task2));
        TaskService service = new TaskService(repo);
        
        Task updatedTask = new Task(task3.getTitle(), task3.getDescription(), LocalDate.now().minusDays(1), task3.getStatus());
        service.updateTask(2, updatedTask);
        TaskServiceTest.assertTrue(service.getAllTasks().equals(Arrays.asList(updatedTask, task1, task2)), "Update TAsk - When Task Due Date Updated, Task List Reordered");
    }
}
