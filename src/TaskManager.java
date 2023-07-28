import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
            while((line = br.readLine()) != null)
            {
                String[] taskInfo = line.split(",");
                String taskId = taskInfo[0].trim();
                int timeToComplete = Integer.parseInt(taskInfo[1].trim());
                Task task = new Task(taskId, timeToComplete);

                if (taskInfo.length > 2 ) {
                    for (int i=2; i<taskInfo.length; i++) {
                        task.getDependencies().add(taskMap.get(taskInfo[i]));
                    }
                }
                taskMap.put(taskId, task);
            }
        }
    }

}
