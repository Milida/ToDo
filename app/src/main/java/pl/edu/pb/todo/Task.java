package pl.edu.pb.todo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "task")
public class Task {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;

    @SerializedName("description")
    private String name;

    @SerializedName("createdAt")
    private String date;

    @SerializedName("completed")
    private boolean done;

    public Task() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate(Date date) {
        this.date = date.toString();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm d MMMM yyyy");
        try {
            Date d = formatter.parse(date);
            return endFormat.format(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
