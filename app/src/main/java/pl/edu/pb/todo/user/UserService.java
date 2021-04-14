package pl.edu.pb.todo.user;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/user/login")
    Call<UserContainer> login(@Body User user);

    @POST("/user/register")
    Call<UserContainer> register(@Body User user);

    @POST("/user/logout")
    Call<UserContainer> logout();
}
