package com.example.todofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewTodoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);
        TextView textView = findViewById(R.id.editTextTextPersonName);
        TimePicker picker = findViewById(R.id.timePicker1);
        picker.setIs24HourView(true);
        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;
                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = picker.getHour();
                    minute = picker.getMinute();
                }
                else{
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }

                if (textView.getText().toString().isEmpty()){
                    Toast.makeText(NewTodoActivity.this, "To do can't be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("name", textView.getText());
                    intent.putExtra("hour", hour);
                    intent.putExtra("minute", minute);
                    startActivity(intent);
                }
            }
        });
    }
}