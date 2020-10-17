package com.example.seniortablet;

import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AnsweringFragment extends Fragment {

    private static final String CALLER = "caller";
    private String callerName;

    TextView callerNameTV;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_answering, container, false);

        callerNameTV = v.findViewById(R.id.who_calling);
        answerLayout = v.findViewById(R.id.answer_layout);
        declineLayout = v.findViewById(R.id.decline_layout);

        callerNameTV.setText(callerName);

        answerLayout.setOnClickListener(answerClick);
        declineLayout.setOnClickListener(declineClick);

        return v;
    }


    private void performClick() {
        try {
            Notification notification = ShareDataSingleton.getInstance().getNotification();
            if (notification.actions != null) {
                notification.actions[1].actionIntent.send();
            }
        } catch (PendingIntent.CanceledException e) {
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
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
        }
    };
}