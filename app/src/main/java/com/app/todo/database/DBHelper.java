package com.app.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.todo.model.Data;
import com.app.todo.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arifkhan on 03/11/16.
 */
public class DBHelper extends SQLiteOpenHelper {

   public static final String DATABASE_NAME = "TODO.db";
   public static final String CONTACTS_TABLE_NAME = "task";
   public static final String CONTACTS_COLUMN_ID = "id";
   public static final String CONTACTS_COLUMN_NAME = "name";
   public static final String CONTACTS_COLUMN_STATE = "state";
   private HashMap hp;

   public DBHelper(Context context)
   {
      super(context, DATABASE_NAME , null, 1);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL(
      "create table task " +
      "(id integer primary key, name text,state integer)"
      );
   }

   public boolean insertTask(Data data)
   {
      for(Task task : data.getData()) {
          SQLiteDatabase db = this.getWritableDatabase();
          ContentValues contentValues = new ContentValues();
          contentValues.put("name", task.getName());
          contentValues.put("state", task.getState());
          contentValues.put("id", task.getId());
          db.insert("task", null, contentValues);
      }
      return true;
   }

    public void insertTask(Task task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", task.getName());
        contentValues.put("state", task.getState());
        //contentValues.put("id", task.getId());
        db.insert("task", null,contentValues);
    }

    public void upDateTask(Task task)
    {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", task.getName());
            contentValues.put("state", task.getState());
            contentValues.put("id", task.getId());
            db.update("task", contentValues,"id = ? ",new String[]{task.getId()+""});
    }


    public Cursor getData(int id){
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from task where id="+id+"", null );
      return res;
   }
   
   public int numberOfRows(){
      SQLiteDatabase db = this.getReadableDatabase();
      int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
      return numRows;
   }

   public Integer deleteTask(Task task)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      return db.delete("task",
      "id = ? ", 
      new String[] { task.getId()+"" });
   }
   
   public ArrayList<Task> getAllPendingTasks()
   {
      ArrayList<Task> array_list = new ArrayList<Task>();
      
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from task where state = 0", null );
      res.moveToFirst();
      
      while(res.isAfterLast() == false){
         Task task = new Task();
         task.setState(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_STATE)));
         task.setId(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)));
         task.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
         array_list.add(task);
         res.moveToNext();
      }
   return array_list;
   }

    public ArrayList<Task> getAllDoneTasks()
    {
        ArrayList<Task> array_list = new ArrayList<Task>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from task where state = 1", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Task task = new Task();
            task.setState(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_STATE)));
            task.setId(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)));
            task.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            array_list.add(task);
            res.moveToNext();
        }
        return array_list;
    }

   @Override
   public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
      //
   }
}