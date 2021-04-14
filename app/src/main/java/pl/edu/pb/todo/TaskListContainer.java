package pl.edu.pb.todo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskListContainer {
    @SerializedName("data")
    private List<Task> tasks;

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
