package com.jaufarusadique.kouspace.kuzgunrocketteam;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        new Handler().postDelayed(new Runnable() {
            public void run () {
                startActivity(new Intent(MainActivity.this,HomeScreen.class));
                finish();
            }
        }, 3000L);
    }
}
