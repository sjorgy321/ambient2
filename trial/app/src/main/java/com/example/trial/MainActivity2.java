package com.example.trial;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Random;
import java.util.UUID;

import kotlinx.coroutines.channels.BroadcastChannel;
 public class MainActivity2 extends AppCompatActivity {

    private TextView heartRateTextView;
    private View redDotView;
    private Handler handler;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private UUID ambientHeartRateServiceUUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB");
    private UUID hrRateCharacteristicUUID = UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB");
    private boolean connecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        heartRateTextView = findViewById(R.id.heartRateTextView);
        redDotView = findViewById(R.id.redDot);
        handler = new Handler();

        // Initialize with "trying to connect" message
        heartRateTextView.setText("Trying to connect...");

        //Start scanning for BLE devices
        startScan();
    }

    private void startScan() {
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                // Check if the scan result matches the Arduino's advertised name
                if (ActivityCompat.checkSelfPermission(MainActivity2.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (result.getDevice().getName() != null && result.getDevice().getName().equals("AmbientMonitor")) {
                    // Stop scanning
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(this);

                    // Set the connecting flag to true
                    connecting = true;

                    // Connect to the Arduino
                    connectToArduino(result.getDevice());
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
    }

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

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    BluetoothGattService service = gatt.getService(ambientHeartRateServiceUUID);
                    if (service != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(hrRateCharacteristicUUID);
                        if (characteristic != null) {
                            gatt.readCharacteristic(characteristic);
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                if (characteristic.getUuid().equals(hrRateCharacteristicUUID)) {
                    int heartRate = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    // Update the UI with the received heart rate
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (connecting) {
                                // If previously connecting, update text to show heart rate
                                heartRateTextView.setText("Heart Rate: " + heartRate + " BPM");
                                connecting = false; // Reset the connecting flag
                            }
                        }
                    });
                }
            }
        });
    }
}


