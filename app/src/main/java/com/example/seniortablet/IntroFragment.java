package com.example.seniortablet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class IntroFragment extends Fragment {

    Button backButton, continueButton, finishButton;
    ImageView dot0, dot1, dot2, dot3, dot00;
    FrameLayout includeLayout;
    RelativeLayout dotsLayout;

    LayoutInflater inflater;

    SharedPreferences trialSharedPref;

    int pageCounter;

    ArrayList<ImageView> dotsList = new ArrayList<>();


    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance() {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStartDateForTrial();

        pageCounter = 0;
    }

    private void setStartDateForTrial() {
        long startEpochTime = System.currentTimeMillis()/1000;

        trialSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = trialSharedPref.edit();
        editor.putLong(getString(R.string.start_date), startEpochTime);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater = _inflater;
        View v = inflater.inflate(R.layout.fragment_intro, container, false);

        backButton = v.findViewById(R.id.back_button);
        continueButton = v.findViewById(R.id.continue_button);
        finishButton = v.findViewById(R.id.finish_button);
        includeLayout = v.findViewById(R.id.include_holder);
        dotsLayout = v.findViewById(R.id.dots_layout);

        dot00 = v.findViewById(R.id.dot_00);
        dot0 = v.findViewById(R.id.dot_0);
        dot1 = v.findViewById(R.id.dot_1);
        dot2 = v.findViewById(R.id.dot_2);
        dot3 = v.findViewById(R.id.dot_3);

        dotsList.add(dot00);
        dotsList.add(dot0);
        dotsList.add(dot1);
        dotsList.add(dot2);
        dotsList.add(dot3);

        backButton.setOnClickListener(backButtonOnClick);
        continueButton.setOnClickListener(continueButtonOnClick);
        finishButton.setOnClickListener(finishButtonOnClick);

        changeLayout(inflater);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    private void changeLayout(LayoutInflater inflater) {
        switch (pageCounter) {
            case 0: {
                backButton.setVisibility(View.INVISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.INVISIBLE);

                dotsLayout.setBackgroundColor(getContext().getColor(R.color.blue_background));

                layoutInflater(inflater, R.layout.intro_00);
                changeDotPosition(pageCounter);

                break;
            }
            case 1: {
                backButton.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.INVISIBLE);

                dotsLayout.setBackgroundColor(getContext().getColor(R.color.background_color));

                layoutInflater(inflater, R.layout.intro_0);
                changeDotPosition(pageCounter);

                break;
            }
            case 2: {
                backButton.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.INVISIBLE);

                layoutInflater(inflater, R.layout.intro_1);
                changeDotPosition(pageCounter);

                break;
            }
            case 3: {
                backButton.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.INVISIBLE);

                layoutInflater(inflater, R.layout.intro_2);
                changeDotPosition(pageCounter);

                break;
            }
            case 4: {
                continueButton.setVisibility(View.INVISIBLE);
                finishButton.setVisibility(View.VISIBLE);

                layoutInflater(inflater, R.layout.intro_3);
                changeDotPosition(pageCounter);

                break;
            }
        }
    }

    private void changeDotPosition(int pageCounter) {
        for (ImageView dot :
                dotsList) {
            dot.setImageDrawable(getContext().getDrawable(R.drawable.white_circle));
        }

        dotsList.get(pageCounter).setImageDrawable(getContext().getDrawable(R.drawable.blue_circle));
    }

    private void layoutInflater(LayoutInflater inflater, int layoutID) {
        includeLayout.removeAllViews();
        View layout = inflater.inflate(layoutID, null);
        includeLayout.addView(layout);
    }

    View.OnClickListener backButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pageCounter--;
            changeLayout(inflater);
        }
    };

    View.OnClickListener continueButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pageCounter++;
            changeLayout(inflater);
        }
    };

    View.OnClickListener finishButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
        }
    };
}