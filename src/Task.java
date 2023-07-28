import java.util.ArrayList;
import java.util.List;

public class Task {
    private String taskId;
    private int timeToComplete;
    private List<Task> dependencies;

    public Task(String taskId, int timeToComplete) {
        this.taskId = taskId;
        this.timeToComplete = timeToComplete;
        this.dependencies = new ArrayList<>();
    }

    public void setTaskId(String newTaskID) {
        this.taskId = newTaskID;
    }

    public void setTimeToComplete(int time) {
        this.timeToComplete = time;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public int getTimeToComplete() {
        return this.timeToComplete;
    }

    public List<Task> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Task> dependencies) {
        this.dependencies = dependencies;
    }
}


}

