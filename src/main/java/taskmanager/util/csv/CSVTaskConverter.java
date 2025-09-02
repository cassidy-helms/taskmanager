package main.java.taskmanager.util.csv;

import java.util.List;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.util.enums.Status;

public class CSVTaskConverter {
	public static String convertToCSV(Task task) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(task.getTitle());
		sb.append(CSVParser.CSV_DELIMITER);
		if(task.getDescription() != null) sb.append(task.getDescription());
		sb.append(CSVParser.CSV_DELIMITER);
		if(task.getDueDate() != null) sb.append(task.getDueDate());
		sb.append(CSVParser.CSV_DELIMITER);
		sb.append(task.getStatus());
		
		return sb.toString();
	}

	public static Task convertFromCSV(List<String> line) {		
		try {
			return new Task(
					CSVWriter.escapeSpecialCharacters(line.get(0)),
					line.get(1).isEmpty() ? null : CSVWriter.escapeSpecialCharacters(line.get(1)),
					line.get(2).isEmpty() ? null : CSVWriter.escapeSpecialCharacters(line.get(2)),
					Status.lookupStatus(line.get(3))
			);
		} catch(Exception e) {
			System.err.println("Error Parsing Task - " + e.getMessage());
		}
		
		return null;
	}
}
