package main.java.taskmanager.util.csv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import main.java.taskmanager.model.Task;
import main.java.taskmanager.util.enums.Status;

public class CSVTaskConverter {
	private static final DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

	/**
	 * Converts a Task to a single row for a CSV file
	 * @param 	task	task object that is to be saved to a CSV file
	 * @return			string representation of a row in a CSV file
	 */
	public static String convertToCSV(Task task) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(CSVWriter.escapeSpecialCharacters(task.getTitle()));
		sb.append(CSVParser.CSV_DELIMITER);
		if(task.getDescription() != null) sb.append(CSVWriter.escapeSpecialCharacters(task.getDescription()));
		sb.append(CSVParser.CSV_DELIMITER);
		if(task.getDueDate() != null) sb.append(dueDateFormat.format(task.getDueDate()));
		sb.append(CSVParser.CSV_DELIMITER);
		sb.append(task.getStatus().getName());
		
		return sb.toString();
	}

	/**
	 * Converts a single row in a CSV file to a Task
	 * @param 	line	string representation of a row in a CSV file
	 * @return			task object parsed from the CSV file row
	 */
	public static Task convertFromCSV(List<String> line) {		
		try {
			return new Task(
					line.get(0),
					line.get(1).isEmpty() ? null : line.get(1),
					line.get(2).isEmpty() ? null : LocalDate.parse(line.get(2), dueDateFormat),
					Status.lookupStatusByName(line.get(3))
			);
		} catch(Exception e) {
			System.err.println("Error Parsing Task - " + e.getMessage());
		}
		
		return null;
	}
}
