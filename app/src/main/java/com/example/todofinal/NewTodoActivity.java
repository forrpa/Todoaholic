package com.example.todofinal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Activity that is going to be called when user presses a button to add a new to do (WIP)
 */
public class NewTodoActivity extends AppCompatActivity {

    /**
     * Gets the name and time of the to do and calls a method to create a new to do
     *
     * @param savedInstanceState application state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);
        TextView newTodoName = findViewById(R.id.newTodoName);
        Button button = findViewById(R.id.newTodoAddBtn);
        TimePicker timePicker = findViewById(R.id.newTodoTimePicker);
        timePicker.setIs24HourView(true);
        button.setOnClickListener(v -> {
            int hour, minute;
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
            newTodo(newTodoName, hour, minute);
        });
    }

    /**
     * Creates a new to do if the name is not empty
     *
     * @param name the name of the to do
     * @param hour the hour the to do is due
     * @param minute the minute the to do is due
     */
    public void newTodo(TextView name, int hour, int minute){
        if (name.getText().toString().isEmpty()){
            Toast.makeText(NewTodoActivity.this, "To do can't be empty!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("name", name.getText());
            intent.putExtra("hour", hour);
            intent.putExtra("minute", minute);
            startActivity(intent);
        }
    }
}