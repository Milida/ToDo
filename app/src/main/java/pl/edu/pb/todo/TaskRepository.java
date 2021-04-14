package pl.edu.pb.todo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> tasks;

    TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getDatabase(application);
        taskDao = database.taskDao();
        tasks = taskDao.findAll();
    }

    LiveData<List<Task>> findAllTasks() {
        return tasks;
    }

    void insert (Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
        });
    }

    void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
        });
    }

    void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.delete(task);
        });
    }

    LiveData<List<Task>> findTaskWithTitle(String name) {
        return taskDao.findTaskWithName(name);
    }

    void deleteAll() {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteAll();
        });
    }
}
