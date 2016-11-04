package com.app.todo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import com.app.todo.R;
import com.app.todo.adapter.ViewPagerAdapter;
import com.app.todo.application.ToDoApp;
import com.app.todo.database.DBHelper;
import com.app.todo.fragment.ItemFragment;
import com.app.todo.interfaces.OnTaskChangedListener;
import com.app.todo.model.Data;
import com.app.todo.model.Task;
import com.app.todo.network.DataApiEndpointInterface;
import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements OnTaskChangedListener {
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBar) ContentLoadingProgressBar progressBar;
    @Inject public Retrofit retrofit;
    @Inject public DBHelper dbHelper;

    private Menu acitvityMenu;
    private ViewPagerAdapter adapter;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ToDoApp)getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("ToDo");
        getDataFromServer();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(null != acitvityMenu) {
                    if (position == 1) {
                        acitvityMenu.getItem(0).setVisible(false);
                    }else if (position == 0) {
                        acitvityMenu.getItem(0).setVisible(true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        acitvityMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    //TODO: check internet available before calling this method
    private void getDataFromServer() {
        DataApiEndpointInterface apiService = retrofit.create(DataApiEndpointInterface.class);
        Call<Data> call = apiService.getData("6890301");
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressBar.setVisibility(View.GONE);
                //int statusCode = response.code();
                data = response.body();
                dbHelper.insertTask(data);
                //setUpViewpager(data);
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpViewpager(Data data){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ItemFragment.newInstance(getPendingTasks(data), ItemFragment.pending), "Pending");
        adapter.addFragment(ItemFragment.newInstance(getDoneTasks(data), ItemFragment.done), "Done");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
    }

    private ArrayList<Task> getPendingTasks(Data data){
        ArrayList<Task> tasks = new ArrayList<>();
        for(Task task : data.getData()){
            if(task.getState()==0){
                tasks.add(task);
            }
        }
        return tasks;
    }

    private ArrayList<Task> getDoneTasks(Data data){
        ArrayList<Task> tasks = new ArrayList<>();
        for(Task task : data.getData()){
            if(task.getState()==1){
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public void onTaskStateChanged(Task task) {
        setUpViewpager(data);
    }

    @Override
    public void onTaskAdded(Task task) {
        data.getData().add(task);
    }
}
