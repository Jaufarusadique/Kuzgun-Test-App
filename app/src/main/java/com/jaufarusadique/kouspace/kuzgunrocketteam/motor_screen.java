package com.jaufarusadique.kouspace.kuzgunrocketteam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class motor_screen extends Fragment {

    public static boolean motorsEnabled = false;

    public ImageView gear_1;
    public ImageView gear_2;
    public ImageView anticlockwise_1;
    public ImageView anticlockwise_2;
    public ImageView clockwise_1;
    public ImageView clockwise_2;
    public ImageView swap_1;
    public ImageView swap_2;
    public SeekBar   pwm_1;
    public SeekBar   pwm_2;
    public CardView  motor_1_settings_button;
    public CardView  motor_2_settings_button;

    private static final String PREF_MOTOR_1_CLOCKWISE          = "pref_motor_1_clockwise";
    private static final String PREF_MOTOR_1_ANTICLOCKWISE      = "pref_motor_1_anticlockwise";
    private static final String PREF_MOTOR_2_CLOCKWISE          = "pref_motor_2_clockwise";
    private static final String PREF_MOTOR_2_ANTICLOCKWISE      = "pref_motor_2_anticlockwise";
    private static final String PREF_MOTOR_1_SWAP               = "pref_motor_1_swap";
    private static final String PREF_MOTOR_2_SWAP               = "pref_motor_2_swap";
    private static       String VAL_MOTOR_1_CLOCKWISE           = "";
    private static       String VAL_MOTOR_1_ANTICLOCKWISE       = "";
    private static       String VAL_MOTOR_2_CLOCKWISE           = "";
    private static       String VAL_MOTOR_2_ANTICLOCKWISE       = "";
    private static       String VAL_MOTOR_1_SWAP                = "";
    private static       String VAL_MOTOR_2_SWAP                = "";
    private RotateAnimation rotate;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public View view;

    private String mParam1;
    private String mParam2;
    boolean threadStatus = true;
    boolean motor_1_clockwise_thread_status     = true;
    boolean motor_1_anticlockwise_thread_status = true;
    boolean motor_2_clockwise_thread_status     = true;
    boolean motor_2_anticlockwise_thread_status = true;

    public motor_screen() {

    }

    public static motor_screen newInstance(String param1, String param2) {
        motor_screen fragment = new motor_screen();
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
        loadPrefs();
        view = inflater.inflate(R.layout.fragment_motor_screen, container, false);
        gear_1=view.findViewById(R.id.gear_1);
        gear_2=view.findViewById(R.id.gear_2);
        anticlockwise_1=view.findViewById(R.id.anticlockwise_1);
        anticlockwise_2=view.findViewById(R.id.anticlockwise_2);
        clockwise_1=view.findViewById(R.id.clockwise_1);
        clockwise_2=view.findViewById(R.id.clockwise_2);
        swap_1=view.findViewById(R.id.swap_1);
        swap_2=view.findViewById(R.id.swap_2);
        pwm_1=view.findViewById(R.id.pwm_1);
        pwm_2=view.findViewById(R.id.pwm_2);
        motor_1_settings_button=view.findViewById(R.id.motor_1_settings_button);
        motor_2_settings_button=view.findViewById(R.id.motor_2_settings_button);

        pwm_1.setMax(255);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pwm_1.setMin(0);
        }
        pwm_2.setMax(255);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pwm_2.setMin(0);
        }

        anticlockwise_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3000-pwm_1.getProgress()*10);
                rotate.setRepeatCount(Animation.INFINITE);
                rotate.setInterpolator(new LinearInterpolator());
                Thread thread = new Thread(new Runnable() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void run() {
                        loadPrefs();
                        while (motor_1_anticlockwise_thread_status) {
                            try {
                                HomeScreen.sendData(VAL_MOTOR_1_ANTICLOCKWISE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    anticlockwise_1.setScaleX(1.5F);
                    anticlockwise_1.setScaleY(1.5F);
                    gear_1.startAnimation(rotate);
                    motor_1_anticlockwise_thread_status = true;
                    thread.start();
                } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                    anticlockwise_1.setScaleX(1);
                    anticlockwise_1.setScaleY(1);
                    gear_1.clearAnimation();
                    motor_1_anticlockwise_thread_status = false;
                    thread.interrupt();
                }
                return true;
            }
        });
        clockwise_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3000-pwm_1.getProgress()*10);
                rotate.setRepeatCount(Animation.INFINITE);
                rotate.setInterpolator(new LinearInterpolator());
                Thread thread = new Thread(new Runnable() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void run() {
                        loadPrefs();
                        while (motor_1_clockwise_thread_status) {
                            try {
                                HomeScreen.sendData(VAL_MOTOR_1_CLOCKWISE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    clockwise_1.setScaleX(1.5F);
                    clockwise_1.setScaleY(1.5F);
                    gear_1.startAnimation(rotate);
                    motor_1_clockwise_thread_status = true;
                    thread.start();
                } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                    clockwise_1.setScaleX(1);
                    clockwise_1.setScaleY(1);
                    gear_1.clearAnimation();
                    motor_1_clockwise_thread_status = false;
                    thread.interrupt();
                }
                return true;
            }
        });
        anticlockwise_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3000-pwm_2.getProgress()*10);
                rotate.setRepeatCount(Animation.INFINITE);
                rotate.setInterpolator(new LinearInterpolator());
                Thread thread = new Thread(new Runnable() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void run() {
                        loadPrefs();
                        while (motor_2_anticlockwise_thread_status) {
                            try {
                                HomeScreen.sendData(VAL_MOTOR_2_ANTICLOCKWISE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    anticlockwise_2.setScaleX(1.5F);
                    anticlockwise_2.setScaleY(1.5F);
                    gear_2.startAnimation(rotate);
                    motor_2_anticlockwise_thread_status = true;
                    thread.start();
                } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                    anticlockwise_2.setScaleX(1);
                    anticlockwise_2.setScaleY(1);
                    gear_2.clearAnimation();
                    motor_2_anticlockwise_thread_status = false;
                    thread.interrupt();
                }
                return true;
            }
        });
        clockwise_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3000-pwm_2.getProgress()*10);
                rotate.setRepeatCount(Animation.INFINITE);
                rotate.setInterpolator(new LinearInterpolator());
                Thread thread = new Thread(new Runnable() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void run() {
                        loadPrefs();
                        while (motor_2_clockwise_thread_status) {
                            try {
                                HomeScreen.sendData(VAL_MOTOR_2_CLOCKWISE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    clockwise_2.setScaleX(1.5F);
                    clockwise_2.setScaleY(1.5F);
                    gear_2.startAnimation(rotate);
                    motor_2_clockwise_thread_status = true;
                    thread.start();
                } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                    clockwise_2.setScaleX(1);
                    clockwise_2.setScaleY(1);
                    gear_2.clearAnimation();
                    motor_2_clockwise_thread_status = false;
                    thread.interrupt();
                }
                return true;
            }
        });
        swap_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPrefs();
                HomeScreen.sendData(VAL_MOTOR_1_SWAP);
            }
        });
        swap_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPrefs();
                HomeScreen.sendData(VAL_MOTOR_2_SWAP);
            }
        });
        pwm_1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pwm_2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        motor_1_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPrefs();
                EditText editText_anticlockwise_1_settings;
                EditText editText_clockwise_1_settings;
                EditText editText_swap_1_settings;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = getLayoutInflater().inflate(R.layout.motor_1_settings,null);
                builder.setView(view1);
                builder.setTitle("Motor 1 - Settings");

                editText_anticlockwise_1_settings  =   view1.findViewById(R.id.editText_anticlockwise_1_settings);
                editText_clockwise_1_settings      =   view1.findViewById(R.id.editText_clockwise_1_settings);
                editText_swap_1_settings           =   view1.findViewById(R.id.editText_swap_1_settings);

                editText_anticlockwise_1_settings.setText(VAL_MOTOR_1_ANTICLOCKWISE);
                editText_clockwise_1_settings.setText(VAL_MOTOR_1_CLOCKWISE);
                editText_swap_1_settings.setText(VAL_MOTOR_1_SWAP);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HomeScreen.SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PREF_MOTOR_1_CLOCKWISE,editText_clockwise_1_settings.getText().toString());
                        editor.putString(PREF_MOTOR_1_ANTICLOCKWISE,editText_anticlockwise_1_settings.getText().toString());
                        editor.putString(PREF_MOTOR_1_SWAP,editText_swap_1_settings.getText().toString());
                        editor.apply();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        motor_2_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPrefs();
                EditText editText_anticlockwise_2_settings;
                EditText editText_clockwise_2_settings;
                EditText editText_swap_2_settings;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = getLayoutInflater().inflate(R.layout.motor_2_settings,null);
                builder.setView(view1);
                builder.setTitle("Motor 2 - Settings");

                editText_anticlockwise_2_settings  =   view1.findViewById(R.id.editText_anticlockwise_2_settings);
                editText_clockwise_2_settings      =   view1.findViewById(R.id.editText_clockwise_2_settings);
                editText_swap_2_settings           =   view1.findViewById(R.id.editText_swap_2_settings);

                editText_anticlockwise_2_settings.setText(VAL_MOTOR_2_ANTICLOCKWISE);
                editText_clockwise_2_settings.setText(VAL_MOTOR_2_CLOCKWISE);
                editText_swap_2_settings.setText(VAL_MOTOR_2_SWAP);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HomeScreen.SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PREF_MOTOR_2_CLOCKWISE,editText_clockwise_2_settings.getText().toString());
                        editor.putString(PREF_MOTOR_2_ANTICLOCKWISE,editText_anticlockwise_2_settings.getText().toString());
                        editor.putString(PREF_MOTOR_2_SWAP,editText_swap_2_settings.getText().toString());
                        editor.apply();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;
    }
    public void loadPrefs(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HomeScreen.SHARED_PREFS,Context.MODE_PRIVATE);
        VAL_MOTOR_1_CLOCKWISE     = sharedPreferences.getString(PREF_MOTOR_1_CLOCKWISE,"");
        VAL_MOTOR_1_ANTICLOCKWISE = sharedPreferences.getString(PREF_MOTOR_1_ANTICLOCKWISE,"");
        VAL_MOTOR_2_CLOCKWISE     = sharedPreferences.getString(PREF_MOTOR_2_CLOCKWISE,"");
        VAL_MOTOR_2_ANTICLOCKWISE = sharedPreferences.getString(PREF_MOTOR_2_ANTICLOCKWISE,"");
        VAL_MOTOR_1_SWAP          = sharedPreferences.getString(PREF_MOTOR_1_SWAP,"");
        VAL_MOTOR_2_SWAP          = sharedPreferences.getString(PREF_MOTOR_2_SWAP,"");
    }
}