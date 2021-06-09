package com.kai.dustbin;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BluetoothService extends Service
{
    public BluetoothService() { }
    BluetoothAdapter bluetoothAdapter;
    private final IBinder binder = new LocalBinder();
    Set<BluetoothDevice> pairedDevices;
    HashSet<String> deviceAddress=new HashSet<String>();
    boolean findPairedDevice=false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if(deviceHardwareAddress!=null)
                    deviceAddress.add(deviceHardwareAddress);
            }
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }
    public class LocalBinder extends Binder
    {
        BluetoothService getService()
        {
            return BluetoothService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent)
    {
       return binder;
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if(deviceAddress.contains(deviceHardwareAddress))
                {
                    Log.d("bluetooth1", deviceName);
                    findPairedDevice=true;
                }
            }
        }
    };
    public boolean findDevices()
    {
        bluetoothAdapter.startDiscovery();
        return findPairedDevice;
    }
}