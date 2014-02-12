package ru.rafaelrs.bluetoothsimpletest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;
import java.util.UUID;

/**
 * Created by rafaelrs on 11.02.14.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private UUID mUUID;
    private UUID serialUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket mmSocket;

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addToLog((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRefresh = (Button)findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(this);
        Button buttonTest = (Button)findViewById(R.id.button_test);
        buttonTest.setOnClickListener(this);
        Button buttonSerial = (Button)findViewById(R.id.button_testserial);
        buttonSerial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_refresh:
                refreshFirstDevice();
                break;
            case R.id.button_test:
                testDevice(mUUID);
                break;
            case R.id.button_testserial:
                testDevice(serialUUID);
                break;
        }

    }

    private void testDevice(UUID testUUID) {

        if (mDevice != null) {
            new BluetoothConnectThread(mDevice, mBluetoothAdapter, testUUID, uiHandler).start();
//            Method m = null;
//            try {
//                /*m = mDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
//
//                // Create socket
//                mmSocket = (BluetoothSocket) m.invoke(mDevice, 1);*/
//
//                mmSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
//                // Connect
//                mmSocket.connect();
//
//            /*} catch (NoSuchMethodException e) {
//                addToLog(e.getMessage());
//            } catch (InvocationTargetException e) {
//                addToLog(e.getMessage());*/
//            } catch (IOException e) {
//                addToLog("Used fallback connection method");
//                try {
//                    Method m2 = mmSocket.getRemoteDevice().getClass().getMethod("createRfcommSocket", new Class<?>[] {Integer.TYPE});
//                    mmSocket = (BluetoothSocket) m2.invoke(mmSocket.getRemoteDevice(), new Object[] {Integer.valueOf(1)});
//                    mmSocket.connect();
//                } catch (Exception e2) {
//                    addToLog(e2.getMessage());
//                    return;
//                }
//            /*} catch (IllegalAccessException e) {
//                addToLog(e.getMessage());*/
//            }
//
//            if (mmSocket.isConnected()) {
//                addToLog("Device successfully connected");
//            }
        } else {
            addToLog("No device found");
        }

    }

    private void clearLog() {
        TextView textLog = (TextView)findViewById(R.id.text_log);
        textLog.setText("");
    }

    private void addToLog(String message) {
        TextView textLog = (TextView)findViewById(R.id.text_log);
        textLog.append(new java.sql.Timestamp(System.currentTimeMillis()).toString() + " - " + message);
        textLog.append("\n");
    }

    private void refreshFirstDevice() {
        clearLog();

        // Get the bonded devices
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        mDevice = null;

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mDevice = device;
                addToLog("Found device: " + mDevice.getName());

                addToLog("Services at device: ");
                ParcelUuid[] serviceUUIDS = mDevice.getUuids();
                mUUID = null;
                for (ParcelUuid serviceUUID : serviceUUIDS) {
                    if (mUUID == null) mUUID = serviceUUID.getUuid();
                    addToLog("\t" + serviceUUID.toString());
                }
                break;
                /*if (device.getName().toUpperCase().contains("PRINTER")) {
                    mDevice = device;
                    break;
                }*/

            }
        }
    }
}
