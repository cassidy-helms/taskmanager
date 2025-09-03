package main.java.taskmanager.util.csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import main.java.taskmanager.model.Task;

public class CSVWriter {	
	public static void writeFile(String path, List<Task> entries) {
		File file = new File(path);
		
		try(PrintWriter pw = new PrintWriter(file)) {
			entries.stream()
				.map(entry -> convertToCSV(entry))
				.forEach(pw::println);
		} catch(IOException e) {
			System.err.println("Error Writing to File: " + path + " with message - " + e.getMessage());
		}
	}
	
	private static String convertToCSV(Task task) {
	    return CSVTaskConverter.convertToCSV(task);
	}
	
	protected static String escapeSpecialCharacters(String data) {
	    if (data.contains(CSVParser.CSV_DELIMITER) || data.contains("\"") || data.contains("'")) {
	    	data = data.replace("\"", "\"\"");
	    	data = "\"" + data + "\"";
	    }
	    return data;
	}
}
