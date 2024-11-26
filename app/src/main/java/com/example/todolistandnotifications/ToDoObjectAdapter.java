package com.example.todolistandnotifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class ToDoObjectAdapter extends ArrayAdapter<ToDoObject> {

    List<ToDoObject> objects;

    public ToDoObjectAdapter(@NonNull Context context, @NonNull List<ToDoObject> objects) {
        super(context, 0, objects);
        this.objects = objects;
    }

    public void updateList(List<ToDoObject> newTasks) {
        objects.clear();
        objects.addAll(newTasks);
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.to_do_element, parent, false);
        }

        ToDoObject toDoObject = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.toDoObjectTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.toDoObjectDescription);
        TextView dateTextView = convertView.findViewById(R.id.toDoObjectTime);
        CheckBox checkbox = convertView.findViewById(R.id.taskCheckBox);

        if (toDoObject != null) {
            titleTextView.setText(toDoObject.getMainTitle());
            descriptionTextView.setText(toDoObject.getDescription());
            dateTextView.setText((toDoObject.getTimeAndDate().length > 3 ? toDoObject.getTimeAndDate()[3] : "00") + ":" + (toDoObject.getTimeAndDate().length > 4 ? toDoObject.getTimeAndDate()[4] : "00"));
            checkbox.setChecked(toDoObject.isCompleted());

            convertView.setOnTouchListener(new SwipeListener(getContext(), convertView));
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), UpdateToDoObjectActivity.class);
                intent.putExtra("toDoObjectTitle", toDoObject.getMainTitle());
                getContext().startActivity(intent);
            });
            checkbox.setOnClickListener(v -> {
                CurrentUserClass cuc = (CurrentUserClass) getContext().getApplicationContext();
                User currentUser = cuc.getUser();

                if(toDoObject.checkDeadline() == -1){
                    Integer[] currentStat = currentUser.getIntegerStatistic();
                    currentStat[2]++;
                    currentUser.setStatistic(currentStat);
                }
                else {
                    Integer[] currentStat = currentUser.getIntegerStatistic();
                    currentStat[0]++;
                    currentUser.setStatistic(currentStat);
                }

                MyDatabaseHelper myDB = new MyDatabaseHelper(getContext());
                myDB.updateStatistic(currentUser.getEmail(), currentUser.getStringStatistic());

                List<ToDoObject> myObjects = currentUser.getTasks();
                myObjects.remove(toDoObject);

                myDB.updateTasks(currentUser.getEmail(), currentUser.fromListToJson(myObjects));
                ((MainMenuActivity) getContext()).refreshData(myObjects);
            });
        }

        return convertView;
    }
}
