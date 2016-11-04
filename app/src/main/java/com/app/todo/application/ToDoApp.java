package com.app.todo.application;

import android.app.Application;

import com.app.todo.dagger.AppComponent;
import com.app.todo.dagger.AppModule;
import com.app.todo.dagger.DBModule;
import com.app.todo.dagger.DaggerAppComponent;
import com.app.todo.dagger.NetModule;

/**
 * Created by arifkhan on 03/11/16.
 */

public class ToDoApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .netModule(new NetModule("https://dl.dropboxusercontent.com/"))
                .appModule(new AppModule(this))
                .dBModule(new DBModule(this))
                .build();

    }

    public AppComponent getAppComponent(){
        return appComponent;
    }
}
