package main.java.taskmanager.util.csv;

import java.util.List;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.util.enums.Status;

public class CSVTaskConverter {
	public static String convertToCSV(Task task) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(CSVWriter.escapeSpecialCharacters(task.getTitle()));
		sb.append(CSVParser.CSV_DELIMITER);
		if(task.getDescription() != null) sb.append(CSVWriter.escapeSpecialCharacters(task.getDescription()));
		sb.append(CSVParser.CSV_DELIMITER);
		if(task.getDueDate() != null) sb.append(CSVWriter.escapeSpecialCharacters(task.getDueDate()));
		sb.append(CSVParser.CSV_DELIMITER);
		sb.append(task.getStatus());
		
		return sb.toString();
	}

	public static Task convertFromCSV(List<String> line) {		
		try {
			return new Task(
					line.get(0),
					line.get(1).isEmpty() ? null : line.get(1),
					line.get(2).isEmpty() ? null : line.get(2),
					Status.lookupStatus(line.get(3))
			);
		} catch(Exception e) {
			System.err.println("Error Parsing Task - " + e.getMessage());
		}
		
		return null;
	}
}
