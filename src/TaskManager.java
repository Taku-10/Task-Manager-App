import java.io.*;
import java.util.*;

public class TaskManager
{
    private Map<String, Task> taskMap;

    public TaskManager()
    {
        taskMap = new HashMap<>();
    }

    public void readTasksFromFile(String fileName) throws IOException {

        taskMap.clear();

        // list to store task information from the file
        List<String[]> taskInfoList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Split the line into taskInfo
                String[] taskInfo = line.split(",");
                // Add the taskInfo array to the taskInfoList
                taskInfoList.add(taskInfo);
            }
        }

        // Add tasks to the taskMap without dependencies
        for (String[] taskInfo : taskInfoList) {
            // Extract taskId and timeToComplete taskInfo array
            String taskId = taskInfo[0].trim();
            int timeToComplete = Integer.parseInt(taskInfo[1].trim());

            Task task = new Task(taskId, timeToComplete);
            // Add task object to the taskMap
            taskMap.put(taskId, task);
        }

        // Process dependencies and add them to tasks
        for (String[] taskInfo : taskInfoList) {

            String taskId = taskInfo[0].trim();

            // Retrieve the corresponding task object from the taskMap using taskId
            Task task = taskMap.get(taskId);

            // Loop through the taskInfo array from dependencies
            for (int i = 2; i < taskInfo.length; i++) {
                // Extract the dependencyId
                String dependencyId = taskInfo[i].trim();

                if (!dependencyId.isEmpty()) {
                    // Get corresponding dependency task object from the taskMap using dependencyId
                    Task dependencyTask = taskMap.get(dependencyId);

                    // Add dependencyTask to the current task's dependencies list
                    task.getDependencies().add(dependencyTask);
                }
            }
        }
    }


    public void addTask(String taskId, int timeToComplete, List<String> dependencies) {
        // Check if the task with the given ID already exists in the taskMap
        if (taskMap.containsKey(taskId)) {
            System.out.println("Task with ID " + taskId + " already exists.");
            return;
        }

        // Check if the Task ID starts with the letter T
        if (!taskId.startsWith("T")) {
            System.out.println("Task ID must start with a T");
            return;
        }

        Task newTask = new Task(taskId, timeToComplete);
        // Loop through each dependency in the List
        for (String dependency : dependencies) {
            // Get the task object that corresponds to the dependency taskId from the taskMap
            Task dependencyTask = taskMap.get(dependency);
            if (dependencyTask != null) {
                // Add the dependencyTask to the dependencies list of the new Task
                newTask.getDependencies().add(dependencyTask);
            } else {
                System.out.println("Dependency with ID " + dependency + " does not exist.");
            }
        }
        // add the newly created task to the taskMap with taskId as the key
        taskMap.put(taskId, newTask);
        System.out.println("Task has been added");
    }

    public void removeTask(String taskId) {
        // Check if the task with the given 'taskId' exists in the taskMap
        if (!taskMap.containsKey(taskId)) {
            System.out.println("Task with id " + taskId + " not found in the project.");
            return;
        }

        // Remove the task with the given id
        taskMap.remove(taskId);

        // Loop through all the remaining tasks in the map
        for (Task task : taskMap.values()) {
            List<Task> dependencies = task.getDependencies();
            int numDependencies = dependencies.size();

            // Temporary list to store dependencies to be removed
            List<Task> dependenciesToRemove = new ArrayList<>();

            // Loop through the dependencies list of the current task
            for (int i = 0; i < numDependencies; i++) {
                Task dependency = dependencies.get(i);

                // Check if the taskId of the dependency matches the taskId to be removed
                if (dependency != null && dependency.getTaskId().equals(taskId)) {
                    // Add the dependency to the temporary list for removal
                    dependenciesToRemove.add(dependency);
                }
            }

            // Remove the dependencies that need to be removed from the current task's dependencies list
            dependencies.removeAll(dependenciesToRemove);
        }

        System.out.println("Task with id "+ taskId + " has been removed");
    }


    public void changeTimeToComplete(String taskId, int newTime)
    {
        // Get task object based on the provided id
        Task task = taskMap.get(taskId);
        // Check if task exists
        if (task!= null)
        {
            // If the task exists update its timeToComplete
            task.setTimeToComplete(newTime);
            System.out.println("Task with id " + taskId + " time changed successfully to " + newTime);
        } else
        {
            System.out.println("Task with id " + taskId + " not found in the project");
            return;
        }
    }

    public void saveTasksToFile(String filename) throws IOException
    {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename)))
        {
            // Loop through each task in the taskMap
            for (Task task : taskMap.values())
            {
                // Write the task information to the file
                bw.write(task.getTaskId() + ", " + task.getTimeToComplete());
                if (!task.getDependencies().isEmpty())
                {
                    // Loop through the dependencies of the current task
                    for (Task dependency : task.getDependencies())
                    {
                        if (dependency != null)
                        {
                            bw.write(", " + dependency.getTaskId());
                        }
                    }
                }

                bw.newLine();
            }
        }
    }


    public List<String> topologicalSort()
    {
        List<String> sortedTasks = new ArrayList<>();
        Set<String> processedTasks = new HashSet<>();
        Deque<String> taskStack = new LinkedList<>();

        // Do a depth-first search on each of the unprocessed tasks in taskMap
        for (Task task : taskMap.values())
        {
            String taskId = task.getTaskId();

            if (!processedTasks.contains(taskId))
            {
                // Recursively perform depth-first search on the task
                depthFirstSearch(taskId, processedTasks, taskStack);
            }
        }

        while (!taskStack.isEmpty())
        {

            sortedTasks.add(0, taskStack.pop());
        }

        return sortedTasks;
    }

    private void depthFirstSearch(String taskId, Set<String> processedTasks, Deque<String> taskStack)
    {
        // Check if this current task has been visited but not fully processed (cycle detection)
        if (processedTasks.contains(taskId))
        {
            return;
        }

        // Mark current task as visited
        processedTasks.add(taskId);

        // Depth-first search on each of the unprocessed dependencies of the current task
        Task task = taskMap.get(taskId);
        if (task != null)
        {
            for (Task dependency : task.getDependencies())
            {
                if (dependency != null)
                {
                    depthFirstSearch(dependency.getTaskId(), processedTasks, taskStack);
                }
            }
        }

        // Push task onto the stack after processing all its dependencies
        taskStack.push(taskId);
    }

    public Map<String, Integer> findEarliestTimes()
    {
        Map<String, Integer> earliestTimes = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // Perform DFS visit on each task
        for (Task task : taskMap.values())
        {
            if (!visited.contains(task.getTaskId()))
            {
                dfsEarliestTimes(task, visited, earliestTimes);
            }
        }

        return earliestTimes;
    }

    private int dfsEarliestTimes(Task task, Set<String> visited, Map<String, Integer> earliestTimes)
    {
        visited.add(task.getTaskId());

        // If the earliest time for this task has already been calculated, return it
        if (earliestTimes.containsKey(task.getTaskId()))
        {
            return earliestTimes.get(task.getTaskId());
        }

        // Calculate the earliest time for this task by considering its dependencies
        int earliestTime = 0;
        for (Task dependency : task.getDependencies())
        {
            if (dependency != null)
            {
                int dependencyTime = dfsEarliestTimes(dependency, visited, earliestTimes);
                earliestTime = Math.max(earliestTime, dependencyTime + dependency.getTimeToComplete());
            }
        }

        // Store the calculated earliest time for this task
        earliestTimes.put(task.getTaskId(), earliestTime);

        return earliestTime;
    }


    public void displayMenu()
    {
        System.out.println("Select a command from the Menu below: ");
        System.out.println("(1) Read tasks from file: ");
        System.out.println("(2) Add task: ");
        System.out.println("(3) Remove task: ");
        System.out.println("(4) Change time to complete: ");
        System.out.println("(5) Save tasks to file: ");
        System.out.println("(6) Find task sequence: ");
        System.out.println("(7) Find earliest times: ");
        System.out.println("(8) Exit: ");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new TaskManager();
        Scanner input = new Scanner(System.in);

        while (true)
        {
            taskManager.displayMenu();

            int choice = input.nextInt();
            input.nextLine();

            switch(choice)
            {
                case 1:
                    System.out.println("Enter the name of the file to read tasks from: ");
                    String fileName = input.nextLine();

                    try
                    {
                        taskManager.readTasksFromFile(fileName);
                        System.out.println("Tasks have been read from the file successfully!");
                    } catch (IOException e)
                    {
                        System.out.println("Error reading tasks from file");
                    }
                    break;

                case 2:
                    System.out.println("Enter the task ID: ");
                    String taskId = input.nextLine();
                    System.out.println("Enter time needed to complete the task: ");
                    int timeToComplete = input.nextInt();
                    input.nextLine();

                    System.out.println("Enter the task dependencies (comma separated if there are any): ");
                    String[] dependenciesArray = input.nextLine().split(",");
                    List<String> dependenciesList = Arrays.asList(dependenciesArray);

                    taskManager.addTask(taskId, timeToComplete, dependenciesList);
                    break;

                case 3:
                    System.out.println("Enter the task ID for the task to remove: ");
                    taskId = input.nextLine();
                    taskManager.removeTask(taskId);
                    break;

                case 4:
                    System.out.println("Enter the task ID to change the time needed to complete: ");
                    taskId = input.nextLine();
                    System.out.println("Enter the new time to complete the task: ");
                    int newTime = input.nextInt();
                    input.nextLine();
                    taskManager.changeTimeToComplete(taskId, newTime);
                    break;

                case 5:
                    System.out.println("Enter the file name to save the tasks to: ");
                    fileName = input.nextLine();
                    try
                    {
                        taskManager.saveTasksToFile(fileName);
                        System.out.println("Tasks have been successfully been saved to " + fileName);
                    } catch(IOException e)
                    {
                        System.out.println("Error saving tasks to file");
                    }
                    break;

                case 6:
                    List<String> taskSequence = taskManager.topologicalSort();
                    System.out.println("Task sequence without violating dependencies: " + taskSequence);
                    break;

                case 7:
                    Map<String, Integer> earliestTimes = taskManager.findEarliestTimes();
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("EarliestTimes.txt")))
                    {
                        for (Map.Entry<String, Integer> entry : earliestTimes.entrySet())
                        {
                            bw.write(entry.getKey() + ", " + entry.getValue());
                            bw.newLine();
                        }
                        System.out.println("Earliest times have been saved to EarliestTimes.txt.");
                    } catch (IOException e)
                    {
                        System.out.println("Error saving earliest times to the file.");
                    }
                    break;

                case 8:
                    System.out.println("Exiting the task manager");
                    return;

                default:
                    System.out.println("Invalid choice. Please select from 1-8 and try again");
                    break;
            }

        }
    }
}
