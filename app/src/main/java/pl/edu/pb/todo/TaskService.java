package pl.edu.pb.todo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskService {
    @GET("/task")
    Call<TaskListContainer> getTasks();

    @POST("/task")
    Call<TaskContainer> addTask(@Body Task task);

    @PUT("/task/{id}")
    Call<TaskContainer> editTask(@Path ("id") String id, @Body EditTask task);

    @DELETE("/task/{id}")
    Call<TaskContainer> deleteTask(@Path("id") String id);
}
