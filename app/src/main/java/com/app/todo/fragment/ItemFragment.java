package com.app.todo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.app.todo.R;
import com.app.todo.activity.MainActivity;
import com.app.todo.adapter.ItemRecyclerViewAdapter;
import com.app.todo.application.ToDoApp;
import com.app.todo.database.DBHelper;
import com.app.todo.database.DataLoader;
import com.app.todo.interfaces.OnListFragmentInteractionListener;
import com.app.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by arifkhan on 03/11/16.
 */
public class ItemFragment extends Fragment implements OnListFragmentInteractionListener, android.view.ActionMode.Callback, LoaderManager.LoaderCallbacks<List<Task>> {

    private List<Task> tasks = new ArrayList<>();
    private int type;
    public static int pending = 0;
    public static int done = 1;
    private ItemRecyclerViewAdapter adapter;
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private boolean isAttached;
    @Inject
    public DBHelper dbHelper;

    public ItemFragment() {
    }

    public static ItemFragment newInstance( int type) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ToDoApp)getActivity().getApplication()).getAppComponent().inject(this);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (type == pending) {
            menu.getItem(0).setVisible(true);
        } else if (type == done){
            menu.getItem(0).setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.editText);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        Task task = new Task();
                                        task.setName(userInput.getText().toString());
                                        task.setState(0);
                                        //tasks.add(task);
                                        dbHelper.insertTask(task);
                                        getLoaderManager().restartLoader(type,null,ItemFragment.this).forceLoad();
                                        //adapter.notifyDataSetChanged();
                                        //((MainActivity)getActivity()).onTaskAdded(task);

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)getActivity()).onSwipeRefresh(swipeRefreshLayout);
            }
        });
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ItemRecyclerViewAdapter(tasks, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        getLoaderManager().initLoader(type, null, this).forceLoad();

        return view;
    }

    //This method is called when fragment is visible to the user.
    //Refreshing the data so that all taks which are moved from pending to done are also shown
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAttached) {
            getLoaderManager().initLoader(type, null, this).forceLoad();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onItemClick(int position, final Task item) {
        if(null != actionMode){
            adapter.toggleSelection(position);
            String title = getString(R.string.selected_count) + " " + adapter.getSelectedItemCount();
            actionMode.setTitle(title);
        }else{
            item.setState(item.getState()==0?1:0);
            dbHelper.upDateTask(item);
            getLoaderManager().restartLoader(type,null,this).forceLoad();
            Snackbar bar = Snackbar.make(recyclerView, "Moved", Snackbar.LENGTH_LONG)
                    .setAction("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            item.setState(item.getState()==0?1:0);
                            dbHelper.upDateTask(item);
                            getLoaderManager().restartLoader(type,null,ItemFragment.this).forceLoad();
                        }
                    });

            bar.setDuration(4000);
            bar.show();
        }
    }

    @Override
    public void onItemLongClick(int position, Task item) {
        actionMode = getActivity().startActionMode(this);
        adapter.toggleSelection(position);
        String title = getString(R.string.selected_count) + " " + adapter.getSelectedItemCount();
        actionMode.setTitle(title);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                List<Integer> selectedItemPositions = adapter.getSelectedItems();
                for (int i=selectedItemPositions.size()-1;i>= 0;i--){
                    adapter.removeData(selectedItemPositions.get(i));
                    dbHelper.deleteTask(tasks.get(selectedItemPositions.get(i)));
                    tasks.remove(selectedItemPositions.get(i));
                    getLoaderManager().restartLoader(type,null,ItemFragment.this).forceLoad();
                }
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        adapter.clearSelections();
    }

    @Override
    public Loader<List<Task>> onCreateLoader(int id, Bundle args) {
        DataLoader loader = null;
        if(type == 0)
            loader = new DataLoader(getActivity(),0);
        else if(type == 1)
            loader = new DataLoader(getActivity(),1);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Task>> loader, List<Task> data) {
        tasks = data;
        adapter.setTasks(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Task>> loader) {

    }
}
