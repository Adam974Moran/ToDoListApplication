package com.example.todolistandnotifications;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;


public class MainMenuActivity extends AppCompatActivity {

    ToDoObjectAdapter adapter;
    LinearLayout menuLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);

        menuLayout = findViewById(R.id.menuLayout);

        ImageButton menuButton = findViewById(R.id.userProfileIcon);
        menuButton.setOnClickListener(v -> toggleMenu());

        Button btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, LogInActivity.class);
            startActivity(intent);
            menuLayout.setVisibility(View.GONE); // Скрыть меню
        });

        Button btnShowStatistics = findViewById(R.id.btnShowStatistics);
        btnShowStatistics.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Статистика");

            CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
            User currentUser = cuc.getUser();

            LayoutInflater inflater = LayoutInflater.from(this);
            View customLayout = inflater.inflate(R.layout.dialog_layout, null);
            TextView mainText = customLayout.findViewById(R.id.mainDialogTextView);
            String stat = "Вовремя выполнено: " + currentUser.getIntegerStatistic()[0]
                    + "\nУдалено: " + currentUser.getIntegerStatistic()[1]
                    + "\nПросрочено: " + currentUser.getIntegerStatistic()[2];
            mainText.setText(stat);

            builder.setView(customLayout);
            builder.setNegativeButton("Окей", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            menuLayout.setVisibility(View.GONE); // Скрыть меню
        });
        
        createNotificationChannel();

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

    private void toggleMenu() {
        if (menuLayout.getVisibility() == View.GONE) {
            menuLayout.setVisibility(View.VISIBLE);
        } else {
            menuLayout.setVisibility(View.GONE);
        }
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
