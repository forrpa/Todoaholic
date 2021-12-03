package com.example.todofinal;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room database to store to do list items
 */
@Database(entities = {Todo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "todo_db";

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                            .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
    public abstract TodoDao getTodoDao();
}
