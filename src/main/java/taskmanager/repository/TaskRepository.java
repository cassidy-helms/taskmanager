package taskmanager.repository;

import java.util.List;

import taskmanager.model.Task;
import taskmanager.util.Config;
import taskmanager.util.csv.CSVParser;
import taskmanager.util.csv.CSVWriter;

public class TaskRepository {
    private final String CSV_FILE_PATH = Config.get("output.file");

    /**
     * Loads tasks from the CSV file.
     *
     * @return List of Tasks loaded from the file
     */
    public List<Task> loadTasks() {
        return CSVParser.parseFile(CSV_FILE_PATH);
    }

    /**
     * Saves the given List of Tasks to the CSV file.
     *
     * @param tasks List of Tasks to save
     */
    public void saveTasks(List<Task> tasks) {
        CSVWriter.writeFile(CSV_FILE_PATH, tasks);
    }
}
