package pl.edu.pb.todo;

import com.google.gson.annotations.SerializedName;

public class TaskContainer {
    @SerializedName("data")
    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
