package com.app.todo.dagger;

import com.app.todo.activity.MainActivity;
import com.app.todo.database.DataLoader;
import com.app.todo.fragment.ItemFragment;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;

/**
 * Created by arifkhan on 03/11/16.
 */

@Singleton
@Component(modules = {NetModule.class, AppModule.class, DBModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(DataLoader dataLoader);
    void inject(ItemFragment itemFragment);
}
