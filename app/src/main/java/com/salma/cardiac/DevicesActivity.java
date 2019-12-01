package com.salma.cardiac;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class DevicesActivity extends AppCompatActivity {

    private static final String TAG = "DevicesActivity";

    Bluetooth bluetooth;
    private ArrayAdapter<String> scanListAdapter;
    private List<BluetoothDevice> scannedDevices;
    private List<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> pairedListAdapter;
    private boolean scanning = false;
    private final Object object = new Object();
    private Map<UUID, String> deviceInfoMap;
    private BluetoothGatt bluetoothGatt;
    private SharedPreferences sharedPreferences;
    private BluetoothGattCallback miBandGattCallBack;
    private BluetoothGattService variableService;
    private boolean isDeviceConnected = false;


    TextView state;
    ProgressBar progress;
    Button scanButton;
    ListView deviceList;
    ListView pairedDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        deviceInfoMap = new HashMap<>();
        scanButton = findViewById(R.id.activity_scan_button);

        progress = findViewById(R.id.activity_scan_progress);
        state = findViewById(R.id.activity_scan_state);
        deviceList = findViewById(R.id.activity_scan_list);
        pairedDeviceList = findViewById(R.id.activity_scan_paired_list);

        // list for paired devices
        pairedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        pairedDeviceList.setAdapter(pairedListAdapter);
        pairedDeviceList.setOnItemClickListener(onPairedListItemClick);


        scanListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        deviceList.setAdapter(scanListAdapter);
        deviceList.setOnItemClickListener(onScanListItemClick);

        // bluetooth lib
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);
        bluetooth.setDiscoveryCallback(discoveryCallback);

        // ui...
        setProgressAndState("", View.GONE);
        scanButton.setEnabled(false);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isDeviceConnected){


                    getDeviceInformation();
                    getGenericAccessInfo();
                    //temporary calls
                    getHeartRate();
                }else bluetooth.startScanning();
            }
        });

        sharedPreferences = getSharedPreferences("MiBandConnectPreferences", Context.MODE_PRIVATE);
        miBandGattCallBack = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch (newState) {
                    case BluetoothGatt.STATE_DISCONNECTED:
                        Log.d("Info", "Device disconnected");
                        scanButton.setText("Get Heart Rate");

                        break;
                    case BluetoothGatt.STATE_CONNECTED: {
                        Log.d("Info", "Connected with device");
                        Log.d("Info", "Discovering services");
                        gatt.discoverServices();
                    }
                    break;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                if (!sharedPreferences.getBoolean("isAuthenticated", false)) {
                    authoriseMiBand();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAuthenticated", true);
                    editor.apply();
                } else
                    Log.d("Info", "Already authenticated");
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

                switch (characteristic.getService().getUuid().toString()) {
                    case UUIDs.DEVICE_INFORMATION_SERVICE_STRING:
//                        handleDeviceInfo(characteristic);
                        break;
                    case UUIDs.GENERIC_ACCESS_SERVICE_STRING:
//                        handleGenericAccess(characteristic);
                        break;
                    case UUIDs.GENERIC_ATTRIBUTE_SERVICE_STRING:
//                        handleGenericAttribute(characteristic);
                        break;
                    case UUIDs.ALERT_NOTIFICATION_SERVICE_STRING:
//                        handleAlertNotification(characteristic);
                        break;
                    case UUIDs.IMMEDIATE_ALERT_SERVICE_STRING:
//                        handleImmediateAlert(characteristic);
                        break;
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                switch (characteristic.getUuid().toString()) {
                    case UUIDs.CUSTOM_SERVICE_AUTH_CHARACTERISTIC_STRING:
//                        executeAuthorisationSequence(characteristic);
                        break;
                    case UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING:
                        handleHeartRateData(characteristic);
                        break;
                }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d("Info", descriptor.getUuid().toString() + " Read");
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d("Info", descriptor.getUuid().toString() + " Written");
            }
        };

    }

    //getting the device details
    private void getDeviceInformation() {
        variableService = bluetoothGatt.getService(UUIDs.DEVICE_INFORMATION_SERVICE);

        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGenericAccessInfo() {
        variableService = bluetoothGatt.getService(UUIDs.GENERIC_ACCESS_SERVICE);
        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
                deviceInfoMap.put(characteristic.getUuid(), characteristic.getStringValue(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getHeartRate() {
        variableService = bluetoothGatt.getService(UUIDs.HEART_RATE_SERVICE);
        BluetoothGattCharacteristic heartRateCharacteristic = variableService.getCharacteristic(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC);
        BluetoothGattDescriptor heartRateDescriptor = heartRateCharacteristic.getDescriptor(UUIDs.HEART_RATE_MEASURMENT_DESCRIPTOR);

        bluetoothGatt.setCharacteristicNotification(heartRateCharacteristic, true);
        heartRateDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(heartRateDescriptor);
    }
    private void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {

        Log.d("Info",characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString());


    }
    private void authoriseMiBand() {
        BluetoothGattService service = bluetoothGatt.getService(UUIDs.CUSTOM_SERVICE_FEE1);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUIDs.CUSTOM_SERVICE_AUTH_CHARACTERISTIC);
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
            if (descriptor.getUuid().equals(UUIDs.CUSTOM_SERVICE_AUTH_DESCRIPTOR)) {
                Log.d("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + descriptor.getUuid().toString());
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
        }

        characteristic.setValue(new byte[]{0x01, 0x8, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45});
        bluetoothGatt.writeCharacteristic(characteristic);
    }
    private AdapterView.OnItemClickListener onPairedListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            if(scanning){
//                bluetooth.stopScanning();
//            }
            bluetoothGatt = scannedDevices.get(i).connectGatt(getApplicationContext(), true, miBandGattCallBack);

        }
    };

    private void displayPairedDevices(){
        pairedDevices = bluetooth.getPairedDevices();
        for(BluetoothDevice device : pairedDevices){
            pairedListAdapter.add(device.getAddress()+" : "+device.getName());
        }
    }

    private void setProgressAndState(String msg, int p) {
        state.setText(msg);
        progress.setVisibility(p);
    }


    private AdapterView.OnItemClickListener onScanListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            if(scanning){
//                bluetooth.stopScanning();
//            }
            setProgressAndState("Pairing...", View.VISIBLE);
            bluetoothGatt = scannedDevices.get(i).connectGatt(getApplicationContext(), true, miBandGattCallBack);
        }
    };



    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        if(bluetooth.isEnabled()){
            displayPairedDevices();
            scanButton.setEnabled(true);
        } else {
            bluetooth.showEnableDialog(DevicesActivity.this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bluetooth.onActivityResult(requestCode, resultCode);
    }

    private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() {
        }

        @Override
        public void onBluetoothOn() {
            displayPairedDevices();
            scanButton.setEnabled(true);
        }

        @Override
        public void onBluetoothTurningOff() {
            scanButton.setEnabled(false);
        }

        @Override
        public void onBluetoothOff() {
        }

        @Override
        public void onUserDeniedActivation() {
            Toast.makeText(DevicesActivity.this, "I need to activate bluetooth...", Toast.LENGTH_SHORT).show();
        }
    };

    private DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
        @Override
        public void onDiscoveryStarted() {
            setProgressAndState("Scanning...", View.VISIBLE);
            scannedDevices = new ArrayList<>();
            scanning = true;
        }

        @Override
        public void onDiscoveryFinished() {
            setProgressAndState("Done.", View.GONE);
            scanning = false;
        }

        @Override
        public void onDeviceFound(BluetoothDevice device) {
            scannedDevices.add(device);
            scanListAdapter.add(device.getAddress()+" : "+device.getName());
        }

        @Override
        public void onDevicePaired(BluetoothDevice device) {
            Toast.makeText(DevicesActivity.this, "Paired !", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {

        }

        @Override
        public void onError(int errorCode) {

        }
    };

}
