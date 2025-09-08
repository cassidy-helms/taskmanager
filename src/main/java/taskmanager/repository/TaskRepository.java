package main.java.taskmanager.repository;

import java.util.List;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.util.csv.CSVParser;
import main.java.taskmanager.util.csv.CSVWriter;

public class TaskRepository {
	private final String CSV_FILE_PATH = "../TaskManager/output/task_manager.csv";

	public List<Task> loadTasks() {
		return CSVParser.parseFile(CSV_FILE_PATH);
	}
	
	public void saveTasks(List<Task> tasks) {
		CSVWriter.writeFile(CSV_FILE_PATH, tasks);
	}
}
