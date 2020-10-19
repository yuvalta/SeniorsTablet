package com.example.seniortablet;

import android.app.Notification;

class ShareDataSingleton {
    private static final ShareDataSingleton ourInstance = new ShareDataSingleton();
    public static final String WA_PACKAGE = "com.whatsapp";

    public static final String NOTIFICATION_REMOVED = "NOTIFICATION_REMOVED";
    public static final String CONNECTED = "CONNECTED";
    public static final String INCOMING_VIDEO = "INCOMING_VIDEO";

    Notification notification;
    boolean isConnected = false;

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

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
