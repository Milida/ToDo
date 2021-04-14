package pl.edu.pb.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_TASK_NAME = "pb.edu.pl.EDIT_TASK_NAME";
    public static final String EXTRA_EDIT_TASK_DONE = "pb.edu.pl.EDIT_TASK_DONE";
    public static final String EXTRA_EDIT_TASK_DATE = "pb.edu.pl.EDIT_TASK_DATE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        EditText editNameEditText = findViewById(R.id.task_name);
        CheckBox editDoneCheckBox = findViewById(R.id.task_done);
        Button dateButton = findViewById(R.id.task_date);
        Date date = new Date();

        if(getIntent().hasExtra(EXTRA_EDIT_TASK_NAME)) {
            Task editedTask = new Task();
            editedTask.setName(getIntent().getStringExtra(EXTRA_EDIT_TASK_NAME));
            editedTask.setDone(getIntent().getBooleanExtra(EXTRA_EDIT_TASK_DONE, false));
            editedTask.setDate(getIntent().getStringExtra(EXTRA_EDIT_TASK_DATE));
            editNameEditText.setText(editedTask.getName());
            editDoneCheckBox.setChecked(editedTask.isDone());
            dateButton.setText(editedTask.getFormattedDate());
            dateButton.setEnabled(false);
        } else {
            SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm d MMMM yyyy");
            dateButton.setText(endFormat.format(date));
        }

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editNameEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String name = editNameEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_TASK_NAME, name);
                boolean done = editDoneCheckBox.isChecked();
                replyIntent.putExtra(EXTRA_EDIT_TASK_DONE, done);
                setResult(RESULT_OK, replyIntent);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
                replyIntent.putExtra(EXTRA_EDIT_TASK_DATE, formatter.format(date));
            }
            finish();
        });
    }
}