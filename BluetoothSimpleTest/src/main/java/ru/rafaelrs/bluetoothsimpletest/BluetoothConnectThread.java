package ru.rafaelrs.bluetoothsimpletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rafaelrs on 12.02.14.
 */
public class BluetoothConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mmAdapter;
    private final UUID MY_UUID;
    private final Handler uiHandler;

    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter mmAdapter, UUID my_uuid, Handler uiHandler) {

        this.mmAdapter = mmAdapter;
        MY_UUID = my_uuid;
        this.uiHandler = uiHandler;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mmAdapter.cancelDiscovery();

        Message msg = new Message();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            msg.obj = connectException.getMessage();
            uiHandler.sendMessage(msg);
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        msg.obj = "Device successfully connected";
        uiHandler.sendMessage(msg);
        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}