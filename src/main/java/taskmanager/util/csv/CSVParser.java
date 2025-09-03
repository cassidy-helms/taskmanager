package main.java.taskmanager.util.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.taskmanager.model.Task;

public class CSVParser {
	protected static final String CSV_DELIMITER = ",";

	public static List<Task> parseFile(String path) {
		List<Task> entries = new ArrayList<>();
		
		try {
            Files.lines(Paths.get(path))
            .map(line -> CSVTaskConverter.convertFromCSV(parseLine(line)))
            .forEach(task -> entries.add(task));
		} catch(IOException e) {
			System.err.println("Error Parsing File: " + path + " with message - " + e.getMessage());
		}
		
		return entries;
	}
	
	private static List<String> parseLine(String line) {
		return Arrays.asList(line.split(CSV_DELIMITER + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));
	}
}
