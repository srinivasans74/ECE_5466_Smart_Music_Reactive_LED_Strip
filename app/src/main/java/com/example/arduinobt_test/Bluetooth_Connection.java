package com.example.arduinobt_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Bluetooth_Connection {
    //Bluetooth
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private static final String TAG = "bt_tag";
    private static final String connected_device_name = new String("DSD TECH HC-05");
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private String status;
    //Client thread
    private static ConnectThread mConnectThread;
    private static final String[] STATUS = {"Checking", "Not Connected", "Connected", "Disabled"};
    private ArrayList<callbackInterface> callbacks = new ArrayList<>();

    //Connected thread
    private static ConnectedThread mConnectedThread;

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @SuppressLint("MissingPermission")
    public Bluetooth_Connection (){
        setStatus(0);
        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            setStatus(3);
            return;
        } else {
            pairedDevices = BA.getBondedDevices();
        }
        for (BluetoothDevice bt : pairedDevices) {
            if (bt.getName().equals(connected_device_name)) {
                start_client(bt, MY_UUID);
                setStatus(2);
                break;
            }
        }
    }

    public void registerCallbackInterface (callbackInterface callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            callback.callback(this);
        }
    }

    public void removeRegisteredCallback (callbackInterface callback) {
        callbacks.remove(callback);
    }

    public String getStatus () {
        return status;
    }

    public boolean write(byte[] bytes){
        if (status.equals(STATUS[2])) {
            mConnectedThread.write(bytes);
            return true;
        } else {
            return false;
        }
    }

    public boolean available () {
        //TODO: check if there is a byte to read from the device,
        // return false if nothing is available, otherwise return true.
        return false;
    }

    public byte read () {
        //TODO: read from the device. Default read is -1
        return -1;
    }

    public void close () {
        mConnectedThread.close();
        mConnectThread.close();
    }

    private void setStatus (int statusCode) {
        status = STATUS[statusCode];
        for (callbackInterface callback: callbacks){
            callback.callback(this);
        }
    }

    private void connected(BluetoothSocket mmSocket) {
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    /*
    public static byte [] ByteArray (byte value)
    {
        return ByteBuffer.allocate(1).put(value).array();
    }
     */

    public void start_client(BluetoothDevice device, UUID my_uuid) {
        mConnectThread = new ConnectThread(device, my_uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                setStatus(1);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void close() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID myUuid) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            Log.d(TAG, "ConnectThread: started.");
            BluetoothSocket tmp = null;
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                setStatus(1);
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BA.cancelDiscovery();
            Log.i(TAG, "RUN mConnectThread ");
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_UUID);
                mmSocket.connect();
                Log.d(TAG, "connection suceess ");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
            connected(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void close() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}
