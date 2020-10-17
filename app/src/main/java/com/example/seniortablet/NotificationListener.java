package com.example.seniortablet;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "UV";
    private static final String WA_PACKAGE = "com.whatsapp";

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Notification Listener connected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(WA_PACKAGE)) return;

        Notification notification = sbn.getNotification();

        ShareDataSingleton.getInstance().setNotification(notification); // share the notification obj tru singleton

        Bundle bundle = notification.extras;

        String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
        String message = bundle.getString(NotificationCompat.EXTRA_TEXT);

        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + message);

        sendMessage(message, from);
    }

    private void sendMessage(String message, String name) {
        Log.d("sender", "start sending");
        Intent intent = new Intent("INCOMING_VIDEO");
        // You can also include some extra data.
        intent.putExtra("message", message);
        intent.putExtra("name", name);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}