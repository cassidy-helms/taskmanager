# TaskManager

TaskManager is a simple Java console application for managing personal tasks.

## Features
Actions:
1. **View Tasks** - Displays All Tasks and allows filtering by By Status (*To Do*, *In Progress*, or *Complete*), by Incomplete Tasks, or by Overdue Tasks
2. **Add Tasks** - Add New Tasks. *Description* and *Due Date* are optional fields. *Due Date* must either by today or a future date.
3. **Mark Tasks as Complete** - Mark Incomplete Task(s) as Complete. Can complete multiple at once.
4. **Update Tasks** - Update Any Task Field
5. **Remove Tasks** - Remove Tasks.  Can remove multiple at once.
6. **Clean Up Tasks** - Remove either All Complete Tasks or All Complete Tasks On or Before a Given Date

Tasks are saved in a CSV file and loaded at application startup. Be sure to save changes before exiting the application, as changes are not automatically saved.

## Assumptions
1. The Task List will remain relatively small. Users should make use of the *Clean Up Tasks* action to remove tasks that have already been completed.
2. Only one user per instance of the application. If multiple users want to use the application, create multiple copies of the source code / jar. Be sure these are saved in separate folders, as the output folder will be created at the same level as the source code / jar.
3. Only one instance of the application will be running at one time. Changes may be overwritten if separate instances are running simultaneously.
4. The application will have permission to create a new folder and read/write to that folder. The folder will be at the same level as the source code / jar.

## Getting Started
Project can be ran by either:
1. Downloading the source code, compiling it, and running src/main/java/taskmanager/TaskManager.java
2. Downloading the source code, compiling it, creating a jar, and running the jar
3. Downloading the Release jar and running it

The output folder will be generated in the same folder the source code / jar is saved in, so it is recommended to place the source code / jar into a folder specific for the Task Manager.

### Prerequisites
- Java 21 or higher

### Build
Compile all source files:
```sh
javac -d bin -cp src/main/java src/main/java/taskmanager/**/*.java
```

### Create Runnable JAR
```sh
jar cfm TaskManager.jar MANIFEST.MF -C bin .
```

### Run
```sh
java -jar TaskManager.jar
```

### Run Tests
```sh
java -cp bin taskmanager.service.TaskServiceTest
```

### Output
Task data is saved to `output/task_manager.csv` in the project directory.

## Project Structure
- `src/main/java/taskmanager/` - Main source code
- `output/` - CSV output directory
- `bin/` - Compiled classes
