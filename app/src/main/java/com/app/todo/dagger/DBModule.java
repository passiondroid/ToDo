package com.app.todo.dagger;

import android.app.Application;
import android.content.Context;

import com.app.todo.database.DBHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by arifkhan on 03/11/16.
 */

@Module
public class DBModule {

    private Context context;

    public DBModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    DBHelper providesDBHelper() {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper;
    }
}