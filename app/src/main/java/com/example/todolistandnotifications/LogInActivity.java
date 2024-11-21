package com.example.todolistandnotifications;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        //setEditTextWidth();

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

    public void setEditTextWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        if (screenWidth > 350) {
            EditText editText = findViewById(R.id.logInUserEmailInputField);
            ViewGroup.LayoutParams params = editText.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            editText.setLayoutParams(params);

            editText = findViewById(R.id.logInPasswordInputField);
            params = editText.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            editText.setLayoutParams(params);
        }
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
