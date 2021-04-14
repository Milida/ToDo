package pl.edu.pb.todo;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pl.edu.pb.todo.user.LoginActivity;
import pl.edu.pb.todo.user.UserContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TASK_ACTIVITY_REQUEST_CODE = 2;
    public static final int DELETE_TASK_ACTIVITY_REQUEST_CODE = 3;
    private static final String KEY_SUBTITLE_VISIBLE = "SUBTITLE";
    private Task actionTask;
    private boolean subtitleVisible = false;
    private List<Task> tasks;
    private static final TaskService taskService = RetrofitInstance.getRetrofitInstance().create(TaskService.class);
    private final TaskAdapter adapter = new TaskAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE_VISIBLE);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.findAll().observe(this, taskList -> {
            adapter.setTasks(taskList);
            tasks = taskList;
            updateSubtitle();
        });

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(view -> {
            if (isNetworkConnected()) {
                Intent intent = new Intent(TaskListActivity.this, EditTaskActivity.class);
                startActivityForResult(intent, NEW_TASK_ACTIVITY_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
            }
        });

        synchronizeWithApi();
    }

    private void synchronizeWithApi() {
        Call<TaskListContainer> taskApiCall = taskService.getTasks();
        taskApiCall.enqueue(new Callback<TaskListContainer>() {
            @Override
            public void onResponse(Call<TaskListContainer> call, Response<TaskListContainer> response) {
                if (response.isSuccessful()) {
                    tasks = response.body().getTasks();
                    for (Task task : tasks) {
                        taskViewModel.insert(task);
                    }
                } else {
                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.api_error),
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TaskListContainer> call, Throwable t) {
                Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.no_internet_connection),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Task task = new Task();
            task.setName(data.getStringExtra(EditTaskActivity.EXTRA_EDIT_TASK_NAME));
            task.setDone(data.getBooleanExtra(EditTaskActivity.EXTRA_EDIT_TASK_DONE, false));
            Call<TaskContainer> taskApiCall = taskService.addTask(task);
            taskApiCall.enqueue(new Callback<TaskContainer>() {
                @Override
                public void onResponse(Call<TaskContainer> call, Response<TaskContainer> response) {
                    if (response.isSuccessful()) {
                        taskViewModel.insert(response.body().getTask());
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.task_added),
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.api_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<TaskContainer> call, Throwable t) {
                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.no_internet_connection),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        } else if(requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE) {
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.empty_not_saved),
                    Snackbar.LENGTH_LONG).show();
        } else if (requestCode == EDIT_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            actionTask.setName(data.getStringExtra(EditTaskActivity.EXTRA_EDIT_TASK_NAME));
            actionTask.setDone(data.getBooleanExtra(EditTaskActivity.EXTRA_EDIT_TASK_DONE, false));
            EditTask task = new EditTask();
            task.setName(actionTask.getName());
            task.setDone(actionTask.isDone());
            Call<TaskContainer> taskApiCall = taskService.editTask(actionTask.getId(), task);
            taskApiCall.enqueue(new Callback<TaskContainer>() {
                @Override
                public void onResponse(Call<TaskContainer> call, Response<TaskContainer> response) {
                    if (response.isSuccessful()) {
                        Task task = response.body().getTask();
                        actionTask.setDate(task.getDate());
                        actionTask.setDone(task.isDone());
                        actionTask.setName(task.getName());
                        taskViewModel.update(actionTask);
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.task_edited),
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.api_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<TaskContainer> call, Throwable t) {
                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.no_internet_connection),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        } else if (requestCode == DELETE_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Call<TaskContainer> taskApiCall = taskService.deleteTask(actionTask.getId());
            taskApiCall.enqueue(new Callback<TaskContainer>() {
                @Override
                public void onResponse(Call<TaskContainer> call, Response<TaskContainer> response) {
                    if (response.isSuccessful()) {
                        taskViewModel.delete(actionTask);
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.task_deleted),
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.api_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<TaskContainer> call, Throwable t) {
                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.no_internet_connection),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_task_menu, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")) {
                    adapter.setTasks(taskViewModel.findAll().getValue());
                } else {
                    taskViewModel.findWithNameLike("%" + newText + "%").observe(TaskListActivity.this, taskList -> {
                        adapter.setTasks(taskList);
                        tasks = taskList;
                    });
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                updateSubtitle();
                return true;
            case R.id.logout:
                Intent intent = new Intent(TaskListActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.LOGGED_EXTRA, false);
                Call<UserContainer> userApiCall = LoginActivity.userService.logout();
                userApiCall.enqueue(new Callback<UserContainer>() {
                    @Override
                    public void onResponse(Call<UserContainer> call, Response<UserContainer> response) {
                        if (response.isSuccessful()) {
                            taskViewModel.deleteAll();
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.api_error),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserContainer> call, Throwable t) {
                        Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.no_internet_connection),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateSubtitle() {
        int todoTasksCount = 0;
        for (Task task: tasks) {
            if (!task.isDone()) {
                todoTasksCount++;
            }
        }
        String subtitle = getString(R.string.subtitle_format, todoTasksCount);
        if (!subtitleVisible) {
            subtitle = null;
        }
        getSupportActionBar().setSubtitle(subtitle);
        invalidateOptionsMenu();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(KEY_SUBTITLE_VISIBLE, subtitleVisible);
        super.onSaveInstanceState(outState);
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView taskNameTextView;
        private final TextView taskDateTextView;
        private final ImageView taskDoneImageView;
        private Task task;

        public TaskHolder (LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            taskNameTextView = itemView.findViewById(R.id.task_item_name);
            taskDateTextView = itemView.findViewById(R.id.task_item_date);
            taskDoneImageView = itemView.findViewById(R.id.task_item_done);
        }

        public void bind(Task task) {
            this.task = task;

            if (task.isDone()) {
                taskDoneImageView.setImageResource(R.drawable.ic_check_circle);
            } else {
                taskDoneImageView.setImageResource(R.drawable.ic_radio_button);
            }

            taskNameTextView.setText(task.getName());
            taskDateTextView.setText(task.getFormattedDate());
        }

        @Override
        public void onClick(View v) {
            if (isNetworkConnected()) {
                actionTask = task;
                Intent intent = new Intent(TaskListActivity.this, EditTaskActivity.class);
                intent.putExtra(EditTaskActivity.EXTRA_EDIT_TASK_NAME, taskNameTextView.getText());
                intent.putExtra(EditTaskActivity.EXTRA_EDIT_TASK_DONE, task.isDone());
                intent.putExtra(EditTaskActivity.EXTRA_EDIT_TASK_DATE, task.getDate());
                startActivityForResult(intent, EDIT_TASK_ACTIVITY_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(TaskListActivity.this).toBundle());
            } else {
                Snackbar.make(v, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(isNetworkConnected()) {
                actionTask = task;
                Intent intent = new Intent(TaskListActivity.this, DeleteTaskActivity.class);
                intent.putExtra(DeleteTaskActivity.EXTRA_DELETE_TASK_NAME, task.getName());
                intent.putExtra(DeleteTaskActivity.EXTRA_DELETE_TASK_DATE, task.getFormattedDate());
                startActivityForResult(intent, DELETE_TASK_ACTIVITY_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(TaskListActivity.this).toBundle());
            } else {
                Snackbar.make(v, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
            }
            return true;
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> tasks;

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TaskHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            if (tasks != null) {
                Task task = tasks.get(position);
                holder.bind(task);
            }
        }

        @Override
        public int getItemCount() {
            if (tasks != null) {
                return tasks.size();
            } else {
                return 0;
            }
        }

        void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }
    }
}