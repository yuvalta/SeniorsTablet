package com.example.seniortablet;

import android.app.Notification;

class ShareDataSingleton {
    private static final ShareDataSingleton ourInstance = new ShareDataSingleton();
    public static final String WA_PACKAGE = "com.whatsapp";

    Notification notification;

    static ShareDataSingleton getInstance() {
        return ourInstance;
    }

    private ShareDataSingleton() {

    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
