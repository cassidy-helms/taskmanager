package main.java.taskmanager.util.enums;

import java.util.HashMap;
import java.util.Map;

public enum Action {
	VIEW("1", "View Tasks", "view tasks"),
	ADD("2", "Add Task", "add task"),
	MARK_COMPLETE("3", "Mark Task as Complete", "mark task complete"),
	UPDATE("4", "Update Task", "update task"),
	REMOVE("5", "Remove Task", "remove task"),
	CLEAN_UP("6", "Clean Up Tasks", "clean up task"),
	SAVE("7", "Save Changes", "save task"),
	EXIT("8", "Exit", "exit");
	
	private final String longName;
	private final String shortName;
	private final String id;
	
	private static final Map<String, Action> idToAction = new HashMap<>();
	
    static {
	    	Action[] values = Action.values();
	    	for(int i = 0; i < values.length; i++) {
	    		Action action = values[i];
	    		idToAction.put(String.valueOf(i+1), action);
	    	}
    }
	
	Action(String id, String longName, String shortName) {
		this.id = id;
		this.longName = longName;
		this.shortName = shortName;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getLongName() {
		return this.longName;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
    public static Action lookupActionById(String id) {
        return idToAction.get(id);
    }
}
