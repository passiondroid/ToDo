package com.app.todo.interfaces;

import com.app.todo.model.Task;

/**
 * Created by arifkhan on 05/11/16.
 */

public interface OnTaskChangedListener {

    public void onTaskStateChanged(Task task);

    public void onTaskAdded(Task task);
}
