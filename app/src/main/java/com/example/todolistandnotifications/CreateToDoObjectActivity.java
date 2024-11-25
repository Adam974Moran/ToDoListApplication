package com.example.todolistandnotifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.todolistandnotifications.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class CreateToDoObjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

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
            CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
            User currentUser = cuc.getUser();
            List<ToDoObject> userListOfObjects = currentUser.getTasks();
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

            MyDatabaseHelper myDB = new MyDatabaseHelper(CreateToDoObjectActivity.this);
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
