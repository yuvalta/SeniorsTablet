package com.example.seniortablet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity {

    TextView dateTV;
    FrameLayout mainBackground;
    BroadcastReceiver _broadcastReceiver;
    Calendar currentTime;
    FragmentManager fragmentManager;
    ImageView connectionIndicator;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = new AlphaAnimation(1, 0);
        mainBackground = findViewById(R.id.main_background);
        dateTV = findViewById(R.id.date_tv);
        connectionIndicator = findViewById(R.id.connection_indicator);

        if (ShareDataSingleton.getInstance().isConnected()) {
            changeIndicator(true);
            stopBlinkingAnimation();
        } else {
            changeIndicator(false);
            startBlinkingLedAnimation();
        }

        fragmentManager = getSupportFragmentManager();

        currentTime = Calendar.getInstance();

        setDateAndBackground();

//        hideNavigationBar();

        registerBroadcastForTime();

        if (isNotificationServiceEnabled()) { // TODO: FIX THIS!
            Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ShareDataSingleton.INCOMING_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectioneReceiver,
                new IntentFilter(ShareDataSingleton.CONNECTED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDismissReceiver,
                new IntentFilter(ShareDataSingleton.NOTIFICATION_REMOVED));

//        openAnswerScreen("", "יובל");
    }

    private void stopBlinkingAnimation() {
        animation.cancel();
    }

    private void startBlinkingLedAnimation() {
        animation.setDuration(200);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        connectionIndicator.startAnimation(animation);
    }


    private void changeIndicator(boolean state) {
        final Drawable colorID = state ? getDrawable(R.drawable.led_circle_green) : getDrawable(R.drawable.led_circle_red);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionIndicator.setBackground(colorID);
            }
        });
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void registerBroadcastForTime() {

        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    changeIndicator(true);
                    stopBlinkingAnimation();
                }
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }


    private void setDateAndBackground() {
//            mainBackground.setBackground(currentTime.get(Calendar.HOUR_OF_DAY) >= 18 ? getDrawable(R.drawable.night) : getDrawable(R.drawable.day));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDateandTime = sdf.format(new Date());

        dateTV.setText(currentDateandTime);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("UV", "mMessageReceiver");

            String message = intent.getStringExtra("message");
            String caller = intent.getStringExtra("name");

            Log.i("UV", "Got message: " + message);

            openAnswerScreen(message, caller);
        }
    };


    private BroadcastReceiver mConnectioneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("UV", "mConnectioneReceiver");
            stopBlinkingAnimation();
            changeIndicator(true);
        }
    };

    private BroadcastReceiver mDismissReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("UV", "mDismissReceiver");
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    };

    private void openAnswerScreen(String message, String caller) { // opens the fragment screen

        try {
//        if (message.contains(getString(R.string.video_message_id))) {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                AnsweringFragment fragment = AnsweringFragment.newInstance(caller);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment, fragment, "calling");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
//        }
//        else {
//            Toast.makeText(this, "התראה שהיא לא וידאו", Toast.LENGTH_SHORT).show();
//       }
        } catch (Exception e) {
            Toast.makeText(this, "בעיה בפתיחת מסך צלצול", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNotificationServiceEnabled() { // checks if we allowed notifications
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
