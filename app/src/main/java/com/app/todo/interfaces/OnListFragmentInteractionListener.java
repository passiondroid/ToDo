package com.app.todo.interfaces;

import com.app.todo.model.Task;

    public interface OnListFragmentInteractionListener {
        void onItemClick(int position, Task item);

        void onItemLongClick(int position, Task item);
    }