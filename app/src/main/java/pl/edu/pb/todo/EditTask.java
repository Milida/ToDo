package pl.edu.pb.todo;

import com.google.gson.annotations.SerializedName;

public class EditTask {
    @SerializedName("description")
    private String name;

    @SerializedName("completed")
    private boolean done;

    EditTask() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
