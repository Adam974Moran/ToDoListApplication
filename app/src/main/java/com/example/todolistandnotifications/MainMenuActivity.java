package com.example.todolistandnotifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.todolistandnotifications.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Calendar;
import java.util.List;


public class MainMenuActivity extends AppCompatActivity {

    ToDoObjectAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);

        createNotificationChannel();

        CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
        User currentUser = cuc.getUser();
        Toast.makeText(this, currentUser.getStringStatistic(), Toast.LENGTH_SHORT).show();


        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        });

        setListOfObjects(listView);

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CreateToDoObjectActivity.class);
            startActivity(intent);
        });
    }

    public void setListOfObjects(ListView listView){

        CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
        User currentUser = cuc.getUser();
        List<ToDoObject> myObjects = currentUser.getTasks();

        if(myObjects != null){
            adapter = new ToDoObjectAdapter(this, myObjects);
            listView.setAdapter(adapter);
        }
    }

    public void refreshData(List<ToDoObject> newTasks) {
        adapter = new ToDoObjectAdapter(this, newTasks);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",
                    "ToDo Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for ToDo reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
