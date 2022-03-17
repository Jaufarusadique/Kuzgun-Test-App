package com.jaufarusadique.kouspace.kuzgunrocketteam;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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

    private static final String motor_1_clockwise = "M1C";
    private static final String motor_1_anticlockwise = "M1AC";
    private static final String motor_2_clockwise = "M2C";
    private static final String motor_2_anticlockwise = "M2AC";
    private static final String motor_1_swap = "M1S";
    private static final String motor_2_swap = "M2S";
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
                        while (motor_1_anticlockwise_thread_status) {
                            try {
                                HomeScreen.sendData("j");
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
                        while (motor_1_clockwise_thread_status) {
                            try {
                                HomeScreen.sendData("j");
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
                        while (motor_2_anticlockwise_thread_status) {
                            try {
                                HomeScreen.sendData("j");
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
                        while (motor_2_clockwise_thread_status) {
                            try {
                                HomeScreen.sendData("j");
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
                HomeScreen.sendData(motor_1_swap);
            }
        });
        swap_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreen.sendData(motor_2_swap);
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

        return view;
    }
}