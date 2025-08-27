package main.java.taskmanager.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
	private String title;
	private String description;
	private Status status;
	private Date dueDate;
	
	public Task(String title, String description, Date dueDate, Status status) {
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.status = status;
	}
	
	public Task(String title, String description, Date dueDate) {
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
	
	public Date getDueDate() {
		return this.dueDate;
	}
	
	public void setDueDate(Date dueDate) {
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
		
		if(this.dueDate != null) {
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			String date = formatter.format(this.dueDate);
			sb.append(", Due Date: " + date);
		}
		
		sb.append(")");
		
		if(this.description != null) {
			sb.append(" - " + this.description);
		}
		
		return sb.toString();
	}
	
	enum Status {
		TODO("To-Do"),
		IN_PROGRESS("In Progress"),
		COMPLETED("Completed");
		
		private final String name;
		
		Status(String name) {
			this.name = name;
		}
		
		String getName() {
			return this.name;
		}
	}
}
