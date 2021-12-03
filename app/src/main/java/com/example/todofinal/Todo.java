package com.example.todofinal;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

/**
 * Entity interface for Room database that represents a to do list item
 * A to do list item has an id, name, the list it belongs to and a boolean for if it is completed or not
 */
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @NonNull
    public String getListName() {
        return listName;
    }

    public void setListName(@NonNull String listName) {
        this.listName = listName;
    }

    public boolean isCompleted(){ return completed; }

    public void setCompleted(boolean completed){
        this.completed = completed;
    }
}
