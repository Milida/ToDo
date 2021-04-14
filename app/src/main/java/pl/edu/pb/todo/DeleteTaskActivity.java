package pl.edu.pb.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteTaskActivity extends AppCompatActivity {
    public static final String EXTRA_DELETE_TASK_NAME = "pb.edu.pl.DELETE_TASK_NAME";
    public static final String EXTRA_DELETE_TASK_DATE = "pb.edu.pl.DELETE_TASK_DATE";

    private TextView taskDetails;
    private Button yes;
    private Button no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_task);

        taskDetails = findViewById(R.id.task_details);

        String name = getIntent().getStringExtra(EXTRA_DELETE_TASK_NAME);
        String date = getIntent().getStringExtra(EXTRA_DELETE_TASK_DATE);

        taskDetails.setText(getString(R.string.task_details, name, date));

        yes = findViewById(R.id.button_yes);
        yes.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            setResult(RESULT_OK, replyIntent);
            finish();
        });
        no = findViewById(R.id.button_no);
        no.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            setResult(RESULT_CANCELED, replyIntent);
            finish();
        });
    }
}