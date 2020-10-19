package com.example.seniortablet;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AnsweringFragment extends Fragment {

    private static final String CALLER = "caller";
    private String callerName;

    TextView callerNameTV, staticCallingTV;
    RelativeLayout answerLayout, declineLayout;

    View v;

    public AnsweringFragment() {
        // Required empty public constructor
    }


    public static AnsweringFragment newInstance(String _callerName) {
        AnsweringFragment fragment = new AnsweringFragment();
        Bundle args = new Bundle();
        args.putString(CALLER, _callerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            callerName = getArguments().getString(CALLER);
        }

//        getCallerIcon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_answering, container, false);

        answerLayout = v.findViewById(R.id.answer_layout);
        declineLayout = v.findViewById(R.id.decline_layout);
        staticCallingTV = v.findViewById(R.id.calling_fixed_text);

        staticCallingTV.setText(String.format(getString(R.string.whos_calling),callerName));

        startBounceAnimation(callerNameTV);
        startBounceAnimation(staticCallingTV);

        answerLayout.setOnClickListener(answerClick);
        declineLayout.setOnClickListener(declineClick);

        return v;
    }

    private void startBounceAnimation(TextView tv) {
        ObjectAnimator animY = ObjectAnimator.ofFloat(tv, "translationY", -50f, 0f);
        animY.setDuration(1500);//1sec
        animY.setInterpolator(new BounceInterpolator());
        animY.setRepeatMode(ValueAnimator.REVERSE);
        animY.setRepeatCount(100);
        animY.start();
    }


    private void performClick() {
        try {
            Notification notification = ShareDataSingleton.getInstance().getNotification();
            if (notification != null) {
                if (notification.actions != null) {
                    notification.actions[1].actionIntent.send();
                }
            } else {
                Toast.makeText(getContext(), "אין התראה", Toast.LENGTH_SHORT).show();
            }
        } catch (PendingIntent.CanceledException e) {
            Toast.makeText(getContext(), "תקלה בלחיצה", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            closeFragment();
        }
    }

    private void getCallerIcon() {
        Bundle extras = ShareDataSingleton.getInstance().getNotification().extras;
        int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);

        try {
            PackageManager manager = getContext().getPackageManager();
            Resources resources = manager.getResourcesForApplication(ShareDataSingleton.getInstance().WA_PACKAGE);

            Drawable icon = resources.getDrawable(iconId);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener answerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            performClick();
        }
    };

    View.OnClickListener declineClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeFragment();
        }
    };

    private void closeFragment() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }
}