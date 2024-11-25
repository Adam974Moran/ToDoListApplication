package com.example.todolistandnotifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    EditText userNameInputField, userSurnameInputField, userEmailInputField, userPasswordInputField, passwordDuplicateInputField;
    Button logInPageButton, registrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Оставь это пустым или добавь свой код, чтобы отключить стандартное поведение кнопки "Назад"
            }
        });

        userNameInputField = findViewById(R.id.userNameInputField);
        userSurnameInputField = findViewById(R.id.userSurnameInputField);
        userEmailInputField = findViewById(R.id.userEmailInputField);
        userPasswordInputField = findViewById(R.id.passwordInputField);
        passwordDuplicateInputField = findViewById(R.id.passwordDuplicateInputField);
        logInPageButton = findViewById(R.id.logInPageButton);
        registrationButton = findViewById(R.id.registrationButton);

        logInPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
            startActivity(intent);
        });

        registrationButton.setOnClickListener(v -> {

                MyDatabaseHelper myDB = new MyDatabaseHelper(RegistrationActivity.this);
                myDB.addUser(userNameInputField.getText().toString().trim(),
                        userSurnameInputField.getText().toString().trim(),
                        userEmailInputField.getText().toString().trim(),
                        userPasswordInputField.getText().toString().trim());
        });
    }

}
