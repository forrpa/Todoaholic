package com.example.todofinal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Completable;

/**
 * Dao interface for a Room database that represents the database interactions
 * A to do list item can be inserted, updated, deleted and retrieved
 * A list can be inserted, updated and retrieved
 */
@Dao
public interface TodoDao {

    @Insert
    void insert(Todo todo);

    @Insert
    void insertAll(Todo... todos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertList(List<Todo> todoList);

    @Update
    void update(Todo todo);

    @Update
    void updateAll(Todo...todos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateList(List<Todo> todoList);

    @Delete
    void delete(Todo todo);

    @Delete
    void deleteAll(ArrayList<Todo> todos);

    @Query("DELETE FROM todo WHERE completed=1")
    void deleteAllCompleted();

    @Query("SELECT DISTINCT list_name FROM todo")
    List<String> getAllCategories();

    @Query("SELECT * FROM todo")
    List<Todo> getAll();

    @Query("SELECT * FROM todo WHERE list_name=:listName")
    List<Todo> getAllByCategory(String listName);

    @Query("SELECT COUNT() FROM todo")
    int getRowCount();

}
