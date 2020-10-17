package com.example.seniortablet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView dateTV;
    LinearLayout mainBackground;
    ArrayList<String> log_list = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.log_listview);
        mainBackground = findViewById(R.id.main_background);
        dateTV = findViewById(R.id.date_tv);

        setDateAndBackground();

        if (isNotificationServiceEnabled()) {
            Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("INCOMING_VIDEO"));


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, log_list);
        listView.setAdapter(adapter);
    }

    private void setDateAndBackground() {
        Calendar currentTime = Calendar.getInstance();

        mainBackground.setBackground(currentTime.get(Calendar.HOUR_OF_DAY) > 17 ? getDrawable(R.drawable.night) : getDrawable(R.drawable.day));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDateandTime = sdf.format(new Date());

        dateTV.setText(currentDateandTime);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");

            log_list.add("Got message: " + message);

            adapter.notifyDataSetChanged();
        }
    };

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
