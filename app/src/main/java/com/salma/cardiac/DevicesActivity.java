package com.salma.cardiac;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class DevicesActivity extends AppCompatActivity {

    Bluetooth bluetooth;
    private ArrayAdapter<String> scanListAdapter;
    private List<BluetoothDevice> scannedDevices;
    private boolean scanning = false;


    TextView state;
    ProgressBar progress;
    Button scanButton;
    ListView deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        scanButton = findViewById(R.id.activity_scan_button);

        progress = findViewById(R.id.activity_scan_progress);
        state = findViewById(R.id.activity_scan_state);
        deviceList = findViewById(R.id.activity_scan_list);



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

                bluetooth.startScanning();
            }
        });
    }


    private void setProgressAndState(String msg, int p) {
        state.setText(msg);
        progress.setVisibility(p);
    }

//    private void displayPairedDevices() {
//        pairedDevices = bluetooth.getPairedDevices();
//        for (BluetoothDevice device : pairedDevices) {
//            pairedListAdapter.add(device.getAddress() + " : " + device.getName());
//        }
//    }


    private AdapterView.OnItemClickListener onPairedListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(scanning){
                bluetooth.stopScanning();
            }

        }
    };

    private AdapterView.OnItemClickListener onScanListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(scanning){
                bluetooth.stopScanning();
            }
            setProgressAndState("Pairing...", View.VISIBLE);
            bluetooth.pair(scannedDevices.get(i));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        if(bluetooth.isEnabled()){
//            displayPairedDevices();
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
//            displayPairedDevices();
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
