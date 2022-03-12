package com.jaufarusadique.kouspace.kuzgunrocketteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class HomeScreen extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    EditText        outputText;
    TextView        inputText;
    CardView        sendButton;
    ScrollView      scrollView;
    DrawerLayout    drawerLayout;
    NavigationView  navigationView;
    Menu            menu;

    private boolean     isBluetoothEnabled    =   false;

    private BluetoothAdapter        bluetoothAdapter;
    private Set<BluetoothDevice>    pairedDevices;
    private BluetoothSocket         socket;
    private String                  myArduino           =   "HC-05";
    private BluetoothDevice         result              =   null;
    private OutputStream            outputStream;
    private InputStream             inputStream;

    volatile boolean stopWorker;
    int              readBufferPosition;
    byte[]           readBuffer;
    Thread           workerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        outputText      = findViewById(R.id.outputText);
        inputText       = findViewById(R.id.inputText);
        sendButton      = findViewById(R.id.sendButton);
        scrollView      = findViewById(R.id.scrollView);
        drawerLayout    = findViewById(R.id.drawer_layout);
        navigationView  = findViewById(R.id.nav_view);

        menu = navigationView.getMenu();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        readFromFile();

        navigationView.setNavigationItemSelectedListener(this);

        bluetoothAdapter    = BluetoothAdapter.getDefaultAdapter();

        //Check if Bluetooth is enabled and enable it

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            do{
                if(bluetoothAdapter.isEnabled()){
                    isBluetoothEnabled=true;
                }
            }while (!isBluetoothEnabled);
        }

        if(bluetoothAdapter.isEnabled()) {
            pairedDevices = bluetoothAdapter.getBondedDevices();
            for(BluetoothDevice bt : pairedDevices){
                menu.add(bt.getName());
            }
            for (int i = 0; i < menu.size(); i++) {
                final MenuItem item = menu.getItem(i);
                final SpannableString s = new SpannableString(item.getTitle());
                s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
                item.setTitle(s);
            }
            for (BluetoothDevice bt : pairedDevices) {
                if (myArduino.equals(bt.getName())) {
                    result = bt;
                    break;
                }
            }
            connect();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
    }

    public void connect(){
        try {
            socket = (BluetoothSocket)result.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(result,1);
        } catch (IllegalAccessException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (InvocationTargetException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (NoSuchMethodException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            socket.connect();
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();;
            }
            try {
                inputStream  = socket.getInputStream();
                beginListenForData();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            connectionDialog();
        }
    }

    public void connectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
        builder.setMessage("Device not connected. Please connect.")
                .setTitle("Connection")
                .setCancelable(true)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        connect();
                    }
                })
                .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void prepareData(String data){
        try {
            byte[] b = data.getBytes();
            outputStream.write(b);
            Toast.makeText(this, "Data Sent", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            if(e.getMessage().contains("Broken pipe")){
                connectionDialog();
            }
        }
    }

    public void sendData() {
            connectionVerification();
            if (socket.isConnected()) {
                prepareData(outputText.getText().toString());

            } else {
                Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
            }

    }

    public void connectionVerification(){
        if(!socket.isConnected()) {
            connectionDialog();
        }

    }

    public void beginListenForData(){
        final Handler handler   = new Handler();
        final byte    delimetre = 10; //ASCII code for newline

        stopWorker          =   false;
        readBufferPosition  = 0;
        readBuffer          = new byte[1024];
        workerThread        =   new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker){
                    try {
                        int bytesAvailable = inputStream.available();
                        if(bytesAvailable>0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for(int i=0; i<bytesAvailable;i++){
                                byte b =    packetBytes[i];
                                if(b == delimetre){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer,0,encodedBytes,0,encodedBytes.length);
                                    final String data = new String(encodedBytes,"US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            inputText.setText(inputText.getText()+"\n"+data);
                                            scrollView.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                                }
                                            });
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        stopWorker = true;
                        Toast.makeText(HomeScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        workerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.disable();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        writeToFile(item.getTitle().toString());
        readFromFile();
        for (BluetoothDevice bt : pairedDevices) {
            if (myArduino.equals(bt.getName())) {
                result = bt;
                break;
            }
        }
        connect();
        return false;
    }

    public void readFromFile(){
        try{
            Toast.makeText(this,"Reading", Toast.LENGTH_SHORT).show();
            FileInputStream is =openFileInput("preferred_device.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while((text=br.readLine())!=null){
                sb.append(text).append("\n");
            }
            Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeToFile(String device){
        FileOutputStream os = null;
        try{
            os = openFileOutput("preferred_device.txt",MODE_PRIVATE);
            os.write(device.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if( os !=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
