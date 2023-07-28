import java.io.*;
import java.util.*;

public class TaskManager
{
    private Map<String, Task> taskMap;

    public TaskManager()
    {
        taskMap = new HashMap<>();
    }

    public void readTasksFromFile(String filename) throws IOException {
        taskMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            // Read each line from the file
            while((line = br.readLine()) != null)
            {
                // Split line into taskInfo
                String[] taskInfo = line.split(",");
                // Extract the taskId and timeToComplete from the array
                String taskId = taskInfo[0].trim();
                int timeToComplete = Integer.parseInt(taskInfo[1].trim());
                Task task = new Task(taskId, timeToComplete);

                for (int i=2; i<taskInfo.length; i++)
                {
                    String dependencyId = taskInfo[i].trim();
                    if (!dependencyId.isEmpty()) {
                        task.getDependencies().add(taskMap.get(dependencyId));
                    }
                }
                // Add task object to the map
                taskMap.put(taskId, task);
            }
        }
    }

    public void addTask(String taskId, int timeToComplete, List<String> dependencies)
    {
        if (!taskId.startsWith("T")) {
            System.out.println(("Task ID must start with the letter T"));
        }

        Task newTask = new Task(taskId, timeToComplete);
        // Loop through each dependency in the List
        for (String dependency: dependencies)
        {
            // Get the task object that corresponds to the dependency taskID from the task map
            // Add it to the dependencies list of the new Task

            newTask.getDependencies().add(taskMap.get(dependency));
        }
        // add the newly created task to the taskMap with taskId as the key
        taskMap.put(taskId, newTask);
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
                    System.out.println("Task has been added");
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

                default:
                    System.out.println("Invalid choice. Please select from 1-8 and try again");
                    break;
            }

        }
    }
}
