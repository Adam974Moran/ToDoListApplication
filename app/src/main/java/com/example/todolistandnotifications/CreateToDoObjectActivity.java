package com.example.todolistandnotifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CreateToDoObjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_to_do_object);

        EditText taskName = findViewById(R.id.toDoObjectName);
        EditText taskDescription = findViewById(R.id.toDoObjectDescription);
        EditText taskDate = findViewById(R.id.toDoObjectDate);
        EditText taskTime = findViewById(R.id.toDoObjectTime);


        Spinner repeatSpinner = findViewById(R.id.repeatSpinner);
        String[] items = {"Каждый день", "Каждые 7 дней", "Каждый месяц", "Не повторять"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        repeatSpinner.setAdapter(adapter);
        repeatSpinner.setOnItemSelectedListener(this);

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateToDoObjectActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });

        Button acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(v -> {
            ToDoObject newObject = new ToDoObject();
            if(!taskName.getText().toString().isEmpty()){
                newObject.setMainTitle(taskName.getText().toString());
            } else {
                Toast.makeText(this, "Поле имени пустое! Введите название!", Toast.LENGTH_SHORT).show();
                return;
            }
            newObject.setDescription(taskDescription.getText().toString());

            String[] stringTimeAndDate = ToDoObject.convertToStringArrayDate(taskDate.getText().toString(), taskTime.getText().toString());
            if(ToDoObject.isTimeAndDateCorrect(stringTimeAndDate)){
                newObject.setTimeAndDate(stringTimeAndDate);
                newObject.setCompleted(false);
            } else {
                Toast.makeText(this, "Поля даты и времени некорректные! Введите заново!", Toast.LENGTH_SHORT).show();
                return;
            }

            newObject.setRepeat(repeatSpinner.getSelectedItem().toString());

            //Обновляем пользователя

            CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
            User currentUser = cuc.getUser();
            List<ToDoObject> userListOfObjects = currentUser.getTasks();
            userListOfObjects.add(newObject);

            currentUser.setTasks(userListOfObjects);
            cuc.setUser(currentUser);
            currentUser.outputAllData();

            //Обновляем базу данных

            MyDatabaseHelper myDB = new MyDatabaseHelper(CreateToDoObjectActivity.this);
            myDB.updateTasks(currentUser.getEmail(), currentUser.fromListToJson(userListOfObjects));
            Intent intent = new Intent(CreateToDoObjectActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Действия при выборе элемента
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Действия, если ничего не выбрано
    }
}
