package com.example.todolistandnotifications;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;


public class LogInActivity extends AppCompatActivity {

    EditText userEmailInputField, userPasswordInputField;
    Button logInButton, registrationPageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Оставь это пустым или добавь свой код, чтобы отключить стандартное поведение кнопки "Назад"
            }
        });

        userEmailInputField = findViewById(R.id.logInUserEmailInputField);
        userPasswordInputField = findViewById(R.id.logInPasswordInputField);
        logInButton = findViewById(R.id.logInButton);
        registrationPageButton = findViewById(R.id.registrationPageButton);

        registrationPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        logInButton.setOnClickListener(v -> {
            if (isUserCorrect(userEmailInputField.getText().toString(), userPasswordInputField.getText().toString())){
                Intent intent = new Intent(LogInActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Wrong User Email Or Password! Try Again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isUserCorrect(String userEmail, String userPassword) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(LogInActivity.this);
        List<User> userList = myDB.readAllUsers();

        for (User user : userList) {
            if (Objects.equals(user.getEmail(), userEmail) && Objects.equals(user.getPassword(), userPassword)) {
                CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
                cuc.setUser(user);
                return true;
            }
        }
        return false;
    }
}
