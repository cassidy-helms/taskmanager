package taskmanager.util.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import taskmanager.model.Task;

public class CSVParser {
	protected static final String CSV_DELIMITER = ",";

	/**
	 * Reads the Tasks saved in the given CSV file
	 * @param 	path	string path to the CSV file where the tasks are stored
	 * @return			List of Tasks that were saved in the CSV File
	 */
	public static List<Task> parseFile(String path) {
		List<Task> entries = new ArrayList<>();
		
		try {
			Path filePath = Paths.get(path);
			
			// If file does not exist yet, return an empty list
			if(!Files.isRegularFile(filePath)) return entries;
			
            Files.lines(filePath)
            .map(line -> CSVTaskConverter.convertFromCSV(parseLine(line)))
            .forEach(task -> entries.add(task));
		} catch(IOException e) {
			System.err.println("Error Parsing File: " + path + " with message - " + e.getMessage());
		}
		
		return entries;
	}
	
	/*
	 * Parses CSV lines to allow for other instances of the delimiter and double quotes
	 * 
	 * ex. Do Laundry, "Wash, Dry, and Fold Laundry",,TO-DO -> ["Do Laundry", "Wash, Dry, and Fold Laundry", null, Status.TODO]
	 */
	private static List<String> parseLine(String line) {
		List<String> parsedLine = Arrays.asList(line.split(CSV_DELIMITER + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));
		
		return parsedLine.stream().map(str -> unescapeSpecialCharacters(str)).collect(Collectors.toList());
	}
	
	/**
	 * Unescapes double quotes
	 * @param value 	the escaped CSV line
	 * @return 			the unescaped line
	 */
	public static String unescapeSpecialCharacters(String value) {
		if (value == null) return null;
		String result = value;
		if (result.length() >= 2 && result.startsWith("\"") && result.endsWith("\"")) {
			result = result.substring(1, result.length() - 1);
			result = result.replace("\"\"", "\"");
		}
		return result;
	}
}
