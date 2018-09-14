package com.demo.myblueteeth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author： Victory
 * @Time： 2018/9/13
 * @QQ： 949021037
 * @Explain： com.demo.myblueteeth
 */
public class BLEUtils {

    private Activity mActivity;
    private UUID uuid;
    private BluetoothAdapter bluetoothAdapter;
    List<BLEBean> mList;

    public BLEUtils(Activity activity) {
        this.mActivity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        mList = new ArrayList<>();
        registerReceiver();
    }

    /**
     * 强制打开蓝牙
     *
     * @return
     */
    public boolean openBLE() {
        boolean isOpen = false;
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(intent, 0x123);
                isOpen = true;
            }
        }
        return isOpen;
    }

    public List<BLEBean> getBleList() {
        return mList;
    }

    //注册广播搜索蓝牙
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mActivity.registerReceiver(mReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 通过广播搜索蓝牙设备
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) return;
                BLEBean blueTeethInfo = new BLEBean();
                blueTeethInfo.setName(scanDevice.getName());
                blueTeethInfo.setAddress(scanDevice.getAddress());
                if (scanDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    blueTeethInfo.setMatching(true);
                } else {
                    blueTeethInfo.setMatching(false);
                }
                mList.add(blueTeethInfo);
                Log.d("dddsss", "onReceive: " + scanDevice.getName() + ",mac：" + scanDevice.getAddress() + ",isMatching："+blueTeethInfo.isMatching());
            }
        }
    };

}
