package com.example.todolistandnotifications;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class User {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String name;
    private String surname;
    private String email;
    private String password;
    private List<ToDoObject> tasks;
    private String tasksJson;
    private Integer[] statistic; // 0 - completed, 1 - deleted, 2 - missed deadline

    public User(String name, String surname, String email, String password, String tasksJson, String statistic) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.surname = surname;
        this.tasksJson = tasksJson;
        if(tasksJson != null) {
            tasks = fromJsonToList(tasksJson);
        } else {
            tasks = new LinkedList<>();
        }
        if (statistic != null) {
            String[] stringStat = statistic.split(",");
            this.statistic = new Integer[stringStat.length]; // Инициализация массива с нужным размером
            for (int i = 0; i < stringStat.length; i++) {
                this.statistic[i] = Integer.parseInt(stringStat[i]);
            }
        } else {
            this.statistic = new Integer[3];
            this.statistic[0] = 0;
            this.statistic[1] = 0;
            this.statistic[2] = 0;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<ToDoObject> getTasks() {
        return tasks;
    }

    public void setTasks(List<ToDoObject> tasks) {
        this.tasks = tasks;
    }

    public Integer[] getIntegerStatistic() {
        return statistic;
    }

    public String getStringStatistic() {
        return this.statistic[0] + "," + this.statistic[1] + "," + this.statistic[2];
    }

    public void setStatistic(Integer[] statistic) {
        this.statistic = statistic;
    }

    public void outputAllData(){
        System.out.println("\nemail: " + this.email +
                "\nlist: " + this.tasks +
                "\njson: " + this.tasksJson + "\n");
    }

    public String fromListToJson (List<ToDoObject> toDoObjectList){
        Gson gson = new Gson();
        return gson.toJson(toDoObjectList);
    }

    public List<ToDoObject> fromJsonToList (String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<ToDoObject>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
