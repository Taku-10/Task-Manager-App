import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                // Any dependencies for the task?
                if (taskInfo.length > 2 ) {
                    // Iterate through the array from index 2 to the end
                    for (int i=2; i<taskInfo.length; i++) {
                        task.getDependencies().add(taskMap.get(taskInfo[i]));
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

    public void removeTask(String taskId)
    {
        // Remove task from map
        Task removedTask = taskMap.remove(taskId);
        // Check if Task was found and removed
        if (removedTask != null)
        {
            // Loop through all the remaining tasks in the task Map
            for (Task task : taskMap.values())
            {
                // Dependencies list of the current task
                List<Task> dependencies = task.getDependencies();
                // Temporary list to store dependencies to be removed
                List<Task> dependenciesToRemove = new ArrayList<>();
                // Loop through the dependencies list of the current list
                for (Task dependency : dependencies) {
                    // Check if the task Id of the dependency matches the taskId to be removed
                    if (dependency.getTaskId().equals(taskId))
                    {
                        // Add the dependency to the temporary list for removal
                        dependenciesToRemove.add(dependency);
                    }
                }
                // Remove the dependencies from the current task's dependencies list
                dependencies.removeAll(dependenciesToRemove);
            }
        }
    }
}
