package com.example.todolistandnotifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class MainMenuActivity extends AppCompatActivity {

    ToDoObjectAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);

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
        adapter.updateList(newTasks);
    }
}
