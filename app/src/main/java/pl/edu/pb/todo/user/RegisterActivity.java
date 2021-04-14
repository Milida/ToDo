package pl.edu.pb.todo.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import pl.edu.pb.todo.R;
import pl.edu.pb.todo.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText name = findViewById(R.id.name);
        EditText age = findViewById(R.id.age);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button register = findViewById(R.id.button_register);

        register.setOnClickListener(v -> {
            User user = new User();
            user.setName(name.getText().toString());
            if (age.getText().toString().equals("")){
                user.setAge(0);
            } else {
                user.setAge(Integer.parseInt(age.getText().toString()));
            }
            user.setEmail(email.getText().toString());
            user.setPassword(password.getText().toString());
            UserService userService = RetrofitInstance.getRetrofitInstance().create(UserService.class);
            Call<UserContainer> userApiCall = userService.register(user);

            userApiCall.enqueue(new Callback<UserContainer>() {
                @Override
                public void onResponse(Call<UserContainer> call, Response<UserContainer> response) {
                    if (response.isSuccessful()) {
                        finish();
                    } else {
                        Snackbar.make(v, getString(R.string.registration_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserContainer> call, Throwable t) {
                    Snackbar.make(v, getString(R.string.api_error),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
}