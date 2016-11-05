package com.app.todo.database;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.app.todo.application.ToDoApp;
import com.app.todo.model.Task;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by arifkhan on 05/11/16.
 */

public class DataLoader extends AsyncTaskLoader<List<Task>> {

    @Inject
    public DBHelper dbHelper;
    private int type;

    public DataLoader(Context context, int type) {
        super(context);
        this.type = type;
        ((ToDoApp)context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public List<Task> loadInBackground() {
        List<Task> tasks = null;
        if(type == 0)
            tasks = dbHelper.getAllPendingTasks();
        else if(type ==1 )
            tasks = dbHelper.getAllDoneTasks();
        return tasks;
    }
}
