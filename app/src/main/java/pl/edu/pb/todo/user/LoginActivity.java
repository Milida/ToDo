package pl.edu.pb.todo.user;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import pl.edu.pb.todo.R;
import pl.edu.pb.todo.RetrofitInstance;
import pl.edu.pb.todo.TaskListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    public static User user;
    public static String token;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPreferences;
    public static final String LOGGED_EXTRA = "LOGGED";
    public static final String TOKEN_KEY = "TOKEN";
    public static UserService userService = RetrofitInstance.getRetrofitInstance().create(UserService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!getIntent().getBooleanExtra(LOGGED_EXTRA, true) && editor != null) {
            editor.remove(TOKEN_KEY);
            editor.apply();
        }
        if (sharedPreferences ==  null) {
            sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        token = sharedPreferences.getString(TOKEN_KEY, null);
        if (token != null) {
            Intent intent = new Intent(LoginActivity.this, TaskListActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(v -> {
            user = new User();
            user.setEmail(email.getText().toString());
            user.setPassword(password.getText().toString());
            Call<UserContainer> userApiCall = userService.login(user);

            userApiCall.enqueue(new Callback<UserContainer>() {
                @Override
                public void onResponse(Call<UserContainer> call, Response<UserContainer> response) {
                    if (response.isSuccessful()) {
                        user = response.body().getUser();
                        token = response.body().getToken();
                        editor.putString(TOKEN_KEY, token);
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, TaskListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(v, getString(R.string.login_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserContainer> call, Throwable t) {
                    Snackbar.make(v, getString(R.string.no_internet_connection),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        });
        Button registerButton = findViewById(R.id.button_register);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }
}