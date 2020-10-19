package com.example.seniortablet;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "UV";

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Notification Listener connected");
        sendMessage("", "", ShareDataSingleton.CONNECTED);
        ShareDataSingleton.getInstance().setConnected(true); // set flag connected tru singleton
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(ShareDataSingleton.getInstance().WA_PACKAGE)) return;

        Notification notification = sbn.getNotification();

        ShareDataSingleton.getInstance().setNotification(notification); // share the notification obj tru singleton

        Bundle bundle = notification.extras;

        String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
        String message = bundle.getString(NotificationCompat.EXTRA_TEXT);

        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + message);

        sendMessage(message, from, ShareDataSingleton.INCOMING_VIDEO);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        sendMessage("", "", ShareDataSingleton.NOTIFICATION_REMOVED);
    }

    @Override
    public void onListenerDisconnected() {
        sendMessage("", "", ShareDataSingleton.DISCONNECTED);
        ShareDataSingleton.getInstance().setConnected(false);
    }

    private void sendMessage(String message, String name, String action) {
        Log.d("sender", "start sending");
        Intent intent = new Intent(action);

        intent.putExtra("message", message);
        intent.putExtra("name", name);
        intent.setAction(action);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}