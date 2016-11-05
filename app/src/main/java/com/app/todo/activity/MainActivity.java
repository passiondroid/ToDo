package com.app.todo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private ViewPagerAdapter adapter;
    private Data data;
    private View swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ToDoApp)getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("ToDo");
        getDataFromServer();
    }


    private void getDataFromServer() {
        DataApiEndpointInterface apiService = retrofit.create(DataApiEndpointInterface.class);
        Call<Data> call = apiService.getData("6890301");
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressBar.setVisibility(View.GONE);
                data = response.body();
                dbHelper.insertTask(data);
                setUpViewpager();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpViewpager(){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ItemFragment.newInstance( ItemFragment.pending), "Pending");
        adapter.addFragment(ItemFragment.newInstance( ItemFragment.done), "Done");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        if(swipeView != null)
            ((SwipeRefreshLayout)swipeView).setRefreshing(false);
    }

    @Override
    public void onTaskStateChanged(Task task) {
        setUpViewpager();
    }

    @Override
    public void onTaskAdded(Task task) {
        data.getData().add(task);
    }

    public void onSwipeRefresh(View view){
        swipeView = view;
        getDataFromServer();
    }
}
