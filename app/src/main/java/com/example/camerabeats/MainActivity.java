package com.example.camerabeats;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice smartwatch;
    private BluetoothSocket bluetoothSocket;

    private TextView heart, watch;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        heart = (TextView) findViewById(R.id.heart);
        watch = (TextView) findViewById(R.id.smartwatch);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }

        // Find the paired smartwatch
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("SmartwatchName")) {
                smartwatch = device;
                break;
            }
        }

        // Establishing Bluetooth connection
        try {
            bluetoothSocket = smartwatch.createRfcommSocketToServiceRecord(UUID.fromString("Bluetooth_UUID"));
            bluetoothSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Start a thread to continuously read heart rate data
        new Thread(new HeartRateReader()).start();
    }

    private class HeartRateReader implements Runnable {
        @Override
        public void run() {
            try {
                InputStream inputStream = bluetoothSocket.getInputStream();
                while(inputStream.available() == 0){
                    inputStream = bluetoothSocket.getInputStream();
                    {
                        int data = inputStream.available();
                        byte[] bytes = new byte[data];
                        inputStream.read(bytes, 0, data);
                        String result = new String(bytes);
                        System.out.println(result);
                        //same as setText ?? HAHAHA
                        //println pa den, miss na eclipse IDE T^T
                        heart.setText(Arrays.toString(bytes));
                    }
                    // Read heart rate data from the input stream
                    //
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

