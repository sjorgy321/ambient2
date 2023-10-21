package com.example.trial;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    private TextView heartRateTextView;
    private View redDotView;
    private Handler handler;
    private Runnable heartRateUpdater;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        heartRateTextView = findViewById(R.id.heartRateTextView);
        redDotView = findViewById(R.id.redDot);
        handler = new Handler();

        heartRateUpdater = new Runnable() {
            // Inside the onCreate method
            ScanCallback scanCallback = new ScanCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    // Check if the scan result matches the Arduino's advertised name
                    if (result.getDevice().getName() != null && result.getDevice().getName().equals("AmbientMonitor")) {
                        // Stop scanning
                        bluetoothAdapter.getBluetoothLeScanner().stopScan(this);

                        // Connect to the Arduino
                        connectToArduino(result.getDevice());
                    }

                }
            };
            

// Start scanning for BLE devices
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);

            @Override
            public void run() {
                // Generate a random heart rate value between 100 and 120
                int randomHeartRate = new Random().nextInt(21) + 100;
                heartRateTextView.setText("Heart Rate: " + randomHeartRate + " BPM");

                // Update the Y position of the red dot based on heart rate
                int dotPositionY = randomHeartRate * 5; // You can adjust the scaling factor
                redDotView.setY(dotPositionY);

                // Schedule the update every 1000ms (1 second)
                handler.postDelayed(this, 100);
            }
        };

        // Start updating the heart rate and red dot position
        handler.post(heartRateUpdater);

    }

    private BluetoothGatt bluetoothGatt;

    @SuppressLint("MissingPermission")
    private void connectToArduino(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Arduino is connected, now you can discover services and characteristics
                    gatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                // Handle service and characteristic discovery here
                // You can read or subscribe to the BLE characteristics to receive data
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the heart rate updating when the activity is destroyed
        handler.removeCallbacks(heartRateUpdater);
    }
}
