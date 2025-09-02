package main.java.taskmanager.util.enums;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	TODO("To-Do"),
	IN_PROGRESS("In Progress"),
	COMPLETED("Completed");
	
	private final String name;
	
    private static final Map<String, Status> nameToStatus = new HashMap<String, Status>();

    static {
        for (Status d : Status.values()) {
        	nameToStatus.put(d.getName(), d);
        }
    }
	
	Status(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
    public static Status lookupStatus(String name) {
        return nameToStatus.get(name);
    }
}