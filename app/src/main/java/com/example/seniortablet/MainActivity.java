package com.example.seniortablet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView dateTV;
    FrameLayout mainBackground;
    BroadcastReceiver _broadcastReceiver;
    Calendar currentTime;
    FragmentManager fragmentManager;
    ImageView connectionIndicator;
    Animation animation;
    SharedPreferences trialSharedPref;

    long SEVEN_DAYS = 60 * 60 * 24 * 7;

    boolean inIntro = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trialSharedPref = getPreferences(Context.MODE_PRIVATE);

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

        registerBroadcastForTime();

        setLocalBroadcasts();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isNotificationServiceEnabled()) {
            openInstructionsForPremission();

        }
        inIntro = false;

        checkDaysForTrialVersion();
    }

    private void checkDaysForTrialVersion() {
        long beginningEpoch = trialSharedPref.getLong(getString(R.string.start_date), 0);
        long currentEpoch = System.currentTimeMillis() / 1000;

        if (currentEpoch == 0) {
            Log.i("UV", "Error in shared preferences get -> start epoch didn't saved, save today date as beginning");

            SharedPreferences.Editor editor = trialSharedPref.edit();
            editor.putLong(getString(R.string.start_date), currentEpoch);
            editor.apply();
        }

        Log.i("UV", "beginning - " + beginningEpoch);
        Log.i("UV", "currentEpoch - " + currentEpoch);


        if (currentEpoch - beginningEpoch > SEVEN_DAYS) { // if the difference bigger then 7 days
            PayDialog alertDialog = new PayDialog(MainActivity.this, false, null);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();

        }
    }

    private void openInstructionsForPremission() {

        if (fragmentManager.getBackStackEntryCount() == 0) {
            IntroFragment fragment = IntroFragment.newInstance();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment, fragment, "intro");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void setLocalBroadcasts() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ShareDataSingleton.INCOMING_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectioneReceiver,
                new IntentFilter(ShareDataSingleton.CONNECTED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDismissReceiver,
                new IntentFilter(ShareDataSingleton.NOTIFICATION_REMOVED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDisconnectedReceiver,
                new IntentFilter(ShareDataSingleton.DISCONNECTED));
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

                    checkDaysForTrialVersion();

                }
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }


    private void setDateAndBackground() {
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
            Log.i("UV", "caller  " + caller);

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
            if (!inIntro) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        }
    };

    private BroadcastReceiver mDisconnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("UV", "mDisconnectedReceiver");
            changeIndicator(false);
            startBlinkingLedAnimation();
        }
    };

    private void openAnswerScreen(String message, String caller) { // opens the fragment screen

        try {
            if (message.contains(getString(R.string.video_message_id))) {
                AnsweringFragment fragment = AnsweringFragment.newInstance(caller);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment, fragment, "calling");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
//                Toast.makeText(this, "התראה שהיא לא וידאו", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i("UV", e.getMessage());
//            Toast.makeText(this, "בעיה בפתיחת מסך צלצול", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNotificationServiceEnabled() { // checks if we allowed notifications
        String theList = android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        String[] theListList = theList.split(":");
        String me = (new ComponentName(this, NotificationListener.class)).flattenToString();

        for (String next : theListList) {
            if (me.equals(next)) {
                return true;
            }
        }
        return false;
    }
}
