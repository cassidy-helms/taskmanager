package main.java.taskmanager.util.enums;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	TODO("1", "To-Do"),
	IN_PROGRESS("2", "In Progress"),
	COMPLETED("3", "Completed");
	
	private final String name;
	private final String id;
	
    private static final Map<String, Status> nameToStatus = new HashMap<>();
    private static final Map<String, Status> idToStatus = new HashMap<>();

    static {
	    	Status[] values = Status.values();
	    	for(int i = 0; i < values.length; i++) {
	    		Status status = values[i];
	    		nameToStatus.put(status.getName(), status);
	    		idToStatus.put(String.valueOf(i+1), status);
	    	}
    }
	
	Status(String id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getId() {
		return this.id;
	}
	
    public static Status lookupStatusByName(String name) {
        return nameToStatus.get(name);
    }
    
    public static Status lookupStatusById(String id) {
        return idToStatus.get(id);
    }
}