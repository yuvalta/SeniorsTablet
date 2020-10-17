package com.example.seniortablet;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView dateTV;
    FrameLayout mainBackground;
    ArrayList<String> log_list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    BroadcastReceiver _broadcastReceiver;
    Calendar currentTime;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.log_listview);
        mainBackground = findViewById(R.id.main_background);
        dateTV = findViewById(R.id.date_tv);

        fragmentManager = getSupportFragmentManager();

        currentTime = Calendar.getInstance();

        setDateAndBackground();

        registerBroadcastForTime();

        if (isNotificationServiceEnabled()) {
            Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("INCOMING_VIDEO"));


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, log_list);
        listView.setAdapter(adapter);

//        openAnswerScreen("ששש", "Yuval");

    }

    private void registerBroadcastForTime() {
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    mainBackground.setBackground(currentTime.get(Calendar.HOUR_OF_DAY) >= 18 ?
                            getDrawable(R.drawable.night) :
                            getDrawable(R.drawable.day));
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }


    private void setDateAndBackground() {
        mainBackground.setBackground(currentTime.get(Calendar.HOUR_OF_DAY) >= 18 ? getDrawable(R.drawable.night) : getDrawable(R.drawable.day));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDateandTime = sdf.format(new Date());

        dateTV.setText(currentDateandTime);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            String caller = intent.getStringExtra("name");

            log_list.add("Got message: " + message);
            Log.i("UV", "Got message: " + message);
            adapter.notifyDataSetChanged();

            openAnswerScreen(message, caller);
        }
    };

    private void openAnswerScreen(String message, String caller) {

        if (message.contains("שיחת וידאו נכנסת")) {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                AnsweringFragment fragment = AnsweringFragment.newInstance(caller);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment, fragment, "calling");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "ENABLED_NOTIFICATION_LISTENERS");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
