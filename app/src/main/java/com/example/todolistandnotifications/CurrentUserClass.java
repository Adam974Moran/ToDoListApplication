package com.example.todolistandnotifications;

import android.app.Application;


public class CurrentUserClass extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
