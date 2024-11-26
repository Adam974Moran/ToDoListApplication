package com.example.todolistandnotifications;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class UpdateToDoObjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_to_do_object);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Оставь это пустым или добавь свой код, чтобы отключить стандартное поведение кнопки "Назад"
            }
        });

        EditText taskName = findViewById(R.id.toDoObjectName);
        EditText taskDescription = findViewById(R.id.toDoObjectDescription);
        EditText taskDate = findViewById(R.id.toDoObjectDate);
        EditText taskTime = findViewById(R.id.toDoObjectTime);

        String objectTitle = getIntent().getStringExtra("toDoObjectTitle");
        CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
        User currentUser = cuc.getUser();
        ToDoObject objectToWork = new ToDoObject();
        int iter = -1;
        List<ToDoObject> userListOfObjects = currentUser.getTasks();
        for (ToDoObject object : userListOfObjects) {
            iter++;
            if(Objects.equals(object.getMainTitle(), objectTitle)){
                objectToWork = object;
                break;
            }
        }
        final ToDoObject oldObject = userListOfObjects.get(iter);

        taskName.setText(objectToWork.getMainTitle());
        taskDescription.setText(objectToWork.getDescription());
        String date = objectToWork.getTimeAndDate()[0] + "." + objectToWork.getTimeAndDate()[1] + "." + objectToWork.getTimeAndDate()[2];
        taskDate.setText(date);
        String time = objectToWork.getTimeAndDate()[3] + ":" + objectToWork.getTimeAndDate()[4];
        taskTime.setText(time);

        Spinner repeatSpinner = findViewById(R.id.repeatSpinner);
        String[] items = {"Каждый день", "Каждые 7 дней", "Каждый месяц", "Не повторять"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        repeatSpinner.setAdapter(adapter);
        repeatSpinner.setOnItemSelectedListener(this);
        Toast.makeText(this, objectToWork.getRepeat(), Toast.LENGTH_SHORT).show();
        switch (objectToWork.getRepeat()) {
            case "Каждый день": {
                repeatSpinner.setSelection(0);
                break;
            }
            case "Каждые 7 дней": {
                repeatSpinner.setSelection(1);
                break;
            }
            case "Каждый месяц": {
                repeatSpinner.setSelection(2);
                break;
            }
            default: {
                repeatSpinner.setSelection(3);
                break;
            }
        }

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateToDoObjectActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });

        Button acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(v -> {
            userListOfObjects.remove(oldObject);
            AlarmHelper.cancelAlarm(this, oldObject.getMainTitle());
            ToDoObject newObject = new ToDoObject();
            for(ToDoObject object: userListOfObjects){
                if(Objects.equals(object.getMainTitle(), taskName.getText().toString())){
                    Toast.makeText(this, "Задача с таким именем уже существует!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if(!taskName.getText().toString().isEmpty()){
                newObject.setMainTitle(taskName.getText().toString());
            } else {
                Toast.makeText(this, "Поле имени пустое! Введите название!", Toast.LENGTH_LONG).show();
                return;
            }
            newObject.setDescription(taskDescription.getText().toString());

            if (taskDate.getText().toString().isEmpty()) {
                Toast.makeText(this, "Вы не ввели дату!", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (taskTime.getText().toString().isEmpty()) {
                Toast.makeText(this, "Вы не ввели время!", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] stringTimeAndDate = ToDoObject.convertToStringArrayDate(taskDate.getText().toString(), taskTime.getText().toString());

            if(ToDoObject.isTimeAndDateCorrect(stringTimeAndDate)){
                newObject.setTimeAndDate(stringTimeAndDate);
                newObject.setCompleted(false);
            }
            else {
                Toast.makeText(this, "Поля даты и времени некорректные! Введите заново!", Toast.LENGTH_LONG).show();
                return;
            }

            newObject.setRepeat(repeatSpinner.getSelectedItem().toString());

            //Обновляем пользователя

            userListOfObjects.add(newObject);

            currentUser.setTasks(userListOfObjects);
            cuc.setUser(currentUser);
            currentUser.outputAllData();

            //Обновляем базу данных

            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateToDoObjectActivity.this);
            myDB.updateTasks(currentUser.getEmail(), currentUser.fromListToJson(userListOfObjects));

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(newObject.getTimeAndDate()[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(newObject.getTimeAndDate()[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(newObject.getTimeAndDate()[2]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(newObject.getTimeAndDate()[3]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(newObject.getTimeAndDate()[4]));
            calendar.set(Calendar.SECOND, 0);

            long triggerAtMillis = calendar.getTimeInMillis();

            long intervalMillis;

            switch (newObject.getRepeat()) {
                case "Каждый день":
                    intervalMillis = AlarmManager.INTERVAL_DAY;
                    break;
                case "Каждые 7 дней":
                    intervalMillis = AlarmManager.INTERVAL_DAY * 7;
                    break;
                case "Каждый месяц":
                    intervalMillis = AlarmManager.INTERVAL_DAY * 30;
                    break;
                default:
                    intervalMillis = 0; // Не повторять
                    break;
            }

            if (intervalMillis > 0) {
                AlarmHelper.setRepeatingAlarm(this, triggerAtMillis, intervalMillis, newObject.getMainTitle(), newObject.getDescription());
            } else {
                AlarmHelper.setAlarm(this, triggerAtMillis, newObject.getMainTitle(), newObject.getDescription());
            }

            // Возврат к MainMenuActivity после установки таймера
            Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
            startActivity(mainMenuIntent);
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
