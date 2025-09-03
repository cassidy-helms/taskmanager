package main.java.taskmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import main.java.taskmanager.util.enums.Status;

public class Task {
	private String title;
	private String description;
	private LocalDate dueDate;
	private Status status;
	
	public Task(String title, String description, LocalDate dueDate, Status status) {
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.status = status;
	}
	
	public Task(String title, String description, LocalDate dueDate) {
		this(title, description, dueDate, Status.TODO);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStatus() {
		return this.status.getName();
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public LocalDate getDueDate() {
		return this.dueDate;
	}
	
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;	
	}
	
	/*
	 * Format: name (Status: status, Due Date: date) - description
	 * ex. Grocery Shopping (Status: To-Do, Due Date: 08/28/2025) - buy ingredients for tacos
	 */
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.title + " (Status: " + this.status.getName());
		
		 DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;		
		 if(this.dueDate != null) {
			sb.append(", Due Date: " + dateFormat.format(this.dueDate));
		}
		
		sb.append(")");
		
		if(!this.description.isEmpty()) {
			sb.append(" - " + this.description);
		}
		
		return sb.toString();
	}
}
