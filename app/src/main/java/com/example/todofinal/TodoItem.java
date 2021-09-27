/* Jennifer McCarthy, jemc7787, 930124-0983 */

package com.example.todofinal;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Objects;

/* Klass som representerar ett list-objekt.
* Har ett namn och listnamn samt en boolean för om den är markerad som klar eller inte. */
public class TodoItem {

    private String name;
    private boolean completed;
    private String listName;

    public TodoItem(String name, String listName) {
        this.name = name;
        completed = false;
        this.listName = listName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem toDoItem = (TodoItem) o;
        return Objects.equals(name, toDoItem.name);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(name, completed);
    }
}

