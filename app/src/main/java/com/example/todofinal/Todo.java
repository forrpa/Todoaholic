package com.example.todofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "todo")
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    @NonNull
    public String name;

    @ColumnInfo(name = "list_name")
    @NonNull
    public String listName;

    @ColumnInfo(name = "completed")
    public boolean completed;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean isCompleted(){ return completed; }

    public void setCompleted(boolean completed){
        this.completed = completed;
    }
}
