package com.example.seniortablet;

import android.app.Notification;

class ShareDataSingleton {
    private static final ShareDataSingleton ourInstance = new ShareDataSingleton();
    public static final String WA_PACKAGE = "com.whatsapp";
    public static final int NOTIFICATION_POSTED = 0;
    public static final int NOTIFICATION_REMOVED = 1;

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
