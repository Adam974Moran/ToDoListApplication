package com.example.todolistandnotifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class SwipeListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private final Context context;
    private final View view;

    public SwipeListener(Context context, View view) {
        this.context = context;
        this.view = view;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 25;
        private static final int SWIPE_VELOCITY_THRESHOLD = 5;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) {
                return false; // Проверка на null
            }
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        public void onSwipeRight() {}

        public void onSwipeLeft() {
            showDialog();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Удаление элемента");

        LayoutInflater inflater = LayoutInflater.from(context);
        View customLayout = inflater.inflate(R.layout.dialog_layout, null);
        TextView mainText = customLayout.findViewById(R.id.mainDialogTextView);
        mainText.setText("Вы действительно хотите удалить это задание?");

        builder.setView(customLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CurrentUserClass cuc = (CurrentUserClass) context.getApplicationContext();
                User currentUser = cuc.getUser();

                Integer[] currentStat = currentUser.getIntegerStatistic();
                currentStat[1]++;
                currentUser.setStatistic(currentStat);

                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                myDB.updateStatistic(currentUser.getEmail(), currentUser.getStringStatistic());

                List<ToDoObject> myObjects = currentUser.getTasks();
                ToDoObject objectToRemove = null;
                for (ToDoObject myObject : myObjects) {
                    if(Objects.equals(myObject.getMainTitle(), ((TextView) view.findViewById(R.id.toDoObjectTitle)).getText().toString())){
                        objectToRemove = myObject;
                    }
                }
                AlarmHelper.cancelAlarm(context, objectToRemove.getMainTitle());
                if(objectToRemove != null){
                    myObjects.remove(objectToRemove);
                }
                myDB.updateTasks(currentUser.getEmail(), currentUser.fromListToJson(myObjects));
                ((MainMenuActivity) context).refreshData(myObjects);
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


