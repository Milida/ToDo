package pl.edu.pb.todo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task book);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM task")
    void deleteAll();

    @Query("SELECT * FROM task ORDER BY name")
    LiveData<List<Task>> findAll();

    @Query("SELECT * FROM task WHERE name LIKE :name")
    LiveData<List<Task>> findTaskWithName(String name);

}
