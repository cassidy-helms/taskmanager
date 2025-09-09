package main.java.taskmanager.util.csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import main.java.taskmanager.model.Task;

public class CSVWriter {	
	/**
	 * Writes Tasks to the given CSV File
	 * @param 	path		string path to the CSV file where the tasks will be stored
	 * @param 	entries		List of Tasks to save to the CSV file 
	 */
	public static void writeFile(String path, List<Task> entries) {
		File file = new File(path);
		
		createFolderIfNotPresent(file);
		
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
	
	/*
	 * Escapes the delimiter and quotes so they can be parsed correctly when reading from the file
	 * ex. 	Wash, Dry, and Fold Laundry -> "Wash, Dry, and Fold Laundry"
	 * 		Write book "How to be More Time Efficient" -> "Write Book ""How To Be More Time Efficient"""
	 */
	protected static String escapeSpecialCharacters(String data) {
	    if (data.contains(CSVParser.CSV_DELIMITER) || data.contains("\"") || data.contains("'")) {
	    	data = data.replace("\"", "\"\"");
	    	data = "\"" + data + "\"";
	    }
	    return data;
	}
	
	private static void createFolderIfNotPresent(File file) {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
	}
}
