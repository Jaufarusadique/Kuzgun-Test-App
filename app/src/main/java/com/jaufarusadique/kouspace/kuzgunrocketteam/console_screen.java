package com.jaufarusadique.kouspace.kuzgunrocketteam;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class console_screen extends Fragment {
    public static View view;
    public static EditText outputText;
    public static TextView inputText;
    public static CardView sendButton;
    public static ScrollView scrollView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public console_screen() {

    }

    public static console_screen newInstance(String param1, String param2) {
        console_screen fragment = new console_screen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view   = inflater.inflate(R.layout.fragment_console_screen, container, false);
        scrollView  = view.findViewById(R.id.scrollView);
        sendButton  = view.findViewById(R.id.sendButton);
        outputText  = view.findViewById(R.id.outputText);
        inputText   = view.findViewById(R.id.inputText);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreen.sendData(outputText.getText().toString());
            }
        });
        return view;
    }
}