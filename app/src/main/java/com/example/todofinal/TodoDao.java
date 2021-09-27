package com.example.todofinal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.reactivex.Completable;

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
    void updateTheList(List<Todo> todoList);

    @Delete
    void delete(Todo todo);

    @Delete
    void deleteAll(ArrayList<Todo> todos);

    @Query("DELETE FROM todo WHERE completed=1")
    void deleteAllCompleted();

    @Query("SELECT DISTINCT list_name FROM todo")
    List<String> getAllListNames();

    @Query("SELECT * FROM todo")
    List<Todo> getAll();

    @Query("SELECT * FROM todo WHERE list_name=:listName")
    List<Todo> getAllByListName(String listName);

    @Query("SELECT COUNT() FROM todo")
    int getRowCount();

}
