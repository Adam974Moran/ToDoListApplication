package com.example.todolistandnotifications;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.TimeZone;

public class ToDoObject {
    private String mainTitle;
    private String description;
    private String[] timeAndDate = new String[5]; // 0 - year, 2 - month, 3  - day, 4 - hours, 5 - minutes
    private String repeat;
    private boolean completed;

    public ToDoObject(String mainTitle, String description, String[] timeAndDate, String repeat, boolean completed) {
        this.mainTitle = mainTitle;
        this.description = description;
        this.timeAndDate = timeAndDate;
        this.repeat = repeat;
        this.completed = completed;
    }

    public ToDoObject() {
    }

    public String[] getTimeAndDate() {
        return timeAndDate;
    }

    public void setTimeAndDate(String[] timeAndDate) {
        this.timeAndDate = timeAndDate;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    private int[] getCurrentTime () {
        int[] currentTime = new int[5];
        currentTime[0] = LocalDateTime.now().getYear();
        currentTime[1] = LocalDateTime.now().getMonth().getValue();
        currentTime[2] = LocalDateTime.now().getDayOfMonth();
        currentTime[3] = LocalDateTime.now().getHour();
        currentTime[4] = LocalDateTime.now().getMinute();
        return currentTime;
    }

    public int checkDeadline(){
        int[] currentTime = getCurrentTime();
        int[] taskTime = convertFromStringTimeToIntArray(this.timeAndDate);
        for(int i = 0; i < 5; i++){
            if (taskTime[i] < currentTime[i]){
                return 1;
            }
        }
        return -1;
    }

    public static String[] parseDate(String date) {
        return date.split("\\.");
    }

    public static String[] parseTime(String time) {
        return time.split(":");
    }

    public static boolean isTimeAndDateCorrect(String[] timeAndDate){
        int[] dateBorderBefore = {LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth().getValue(),
                LocalDateTime.now().getDayOfMonth(),
                LocalDateTime.now().getHour(),
                LocalDateTime.now().getMinute()};
        System.err.println();
        int[] dateBorderAfter = {2099, 12, 1, 23, 59};
        System.err.println();
        System.err.println("\nBefore: " + Arrays.toString(dateBorderBefore) +
                "\nAfter: " + Arrays.toString(dateBorderAfter)+
                "\nTimeNadDate: " + Arrays.toString(timeAndDate) + "\n");

        for(int i = 0; i < 5; i++){
            if(Integer.parseInt(timeAndDate[i]) < dateBorderBefore[i]) {
                return false;
            }
            else if(Integer.parseInt(timeAndDate[i]) > dateBorderBefore[i]){
                break;
            }

        }
        for(int i = 0; i < 5; i++){
            if(Integer.parseInt(timeAndDate[i]) > dateBorderAfter[i]){
                return false;
            }
            else if(Integer.parseInt(timeAndDate[i]) < dateBorderAfter[i]) {
                return true;
            }
        }
        return true;
    }

    public static String[] convertToStringArrayDate (String date, String time){
        String[] stringDate = parseDate(date);
        String[] stringTime = parseTime(time);
        String[] timeAndDate = {"0000", "00", "00", "99", "99"};
        if(stringDate.length != 3 || stringTime.length != 2){
            return timeAndDate;
        }
        int j = 0;
        for(int i = 0; i < stringDate.length; i++){
            if(stringDate[i].length() < 2){
                timeAndDate[i] = "0" + stringDate[i];
            }
            else{
                timeAndDate[j] = stringDate[i];
            }
            j++;
        }
        j = 3;
        for(int i = 0; i < stringTime.length; i++){
            if(stringTime[i].length() < 2){
                timeAndDate[j] = "0" + stringTime[i];
            }
            else{
                timeAndDate[j] = stringTime[i];
            }
            j++;
        }
        return timeAndDate;
    }

    public static int[] convertFromStringTimeToIntArray (String[] timeAndDate){
        int[] intTime = new int[5];
        for (int i = 0; i < 5; i++){
            intTime[i] = Integer.parseInt(timeAndDate[i]);
        }
        return intTime;
    }
}
