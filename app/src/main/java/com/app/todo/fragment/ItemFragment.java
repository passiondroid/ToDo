package com.app.todo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.app.todo.interfaces.OnListFragmentInteractionListener;
import com.app.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements OnListFragmentInteractionListener, android.view.ActionMode.Callback {

    private List<Task> tasks;
    private int type;
    public static int pending = 1;
    public static int done = 2;
    private ItemRecyclerViewAdapter adapter;
    private ActionMode actionMode;

    public ItemFragment() {
    }

    public static ItemFragment newInstance(ArrayList<Task> tasks, int type) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("task", tasks);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tasks = (List<Task>) getArguments().get("task");
            type = getArguments().getInt("type");
        }
        if (type == pending) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (type == pending) {
            menu.getItem(0).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.editText);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        Task task = new Task();
                                        task.setName(userInput.getText().toString());
                                        task.setState(0);
                                        tasks.add(task);
                                        adapter.notifyDataSetChanged();
                                        ((MainActivity)getActivity()).onTaskAdded(task);

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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter = new ItemRecyclerViewAdapter(tasks, this);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    /*@Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    @Override
    public void onItemClick(int position, Task item) {
        if(null != actionMode){
            adapter.toggleSelection(position);
            String title = getString(R.string.selected_count) + " " + adapter.getSelectedItemCount();
            actionMode.setTitle(title);
        }else{
            item.setState(item.getState()==0?1:0);
            ((MainActivity)getActivity()).onTaskStateChanged(item);
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
}
