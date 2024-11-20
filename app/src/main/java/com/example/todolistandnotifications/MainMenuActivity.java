package com.example.todolistandnotifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class MainMenuActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureDetector = new GestureDetector(this, this);

        setListOfObjects();

        

        CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
        User currentUser = cuc.getUser();

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CreateToDoObjectActivity.class);
            startActivity(intent);
        });
    }

    public void setListOfObjects(){
        ListView listView = findViewById(R.id.listView);

        CurrentUserClass cuc = (CurrentUserClass) getApplicationContext();
        User currentUser = cuc.getUser();
        List<ToDoObject> myObjects = currentUser.getTasks();

        if(myObjects != null){
            ToDoObjectAdapter adapter = new ToDoObjectAdapter(this, myObjects);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        final int SWIPE_THRESHOLD = 100;
        final int SWIPE_VELOCITY_THRESHOLD = 100;

        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                } else {
                    onSwipeLeft();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void onSwipeLeft() {
        Intent intent = new Intent(MainMenuActivity.this, CreateToDoObjectActivity.class);
        startActivity(intent);
    }



}
