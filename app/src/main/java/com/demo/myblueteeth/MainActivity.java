package com.demo.myblueteeth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnGetBuleTeeth)
    Button btnGetBuleTeeth;
    @BindView(R.id.rvList)
    RecyclerView rvList;
    BluetoothAdapter bluetoothAdapter;
    private MianAdapter mianAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mianAdapter = new MianAdapter();
        rvList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        rvList.setAdapter(mianAdapter);
    }

    @OnClick({R.id.btnGetBuleTeeth, R.id.btnIsOpen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnIsOpen:
                mBuleTeethIsOpen();
                break;
            case R.id.btnGetBuleTeeth:
                bluetoothAdapter.startDiscovery();
                break;
            default:
                break;
        }
    }


    /**
     * 判断蓝牙是否打开
     */
    private boolean mBuleTeethIsOpen() {
        boolean isOpen = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "蓝牙已关闭", Toast.LENGTH_LONG).show();
            }

            // 判断是否打开蓝牙
            if (!bluetoothAdapter.isEnabled()) {
                //弹出对话框提示用户是后打开
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 0x123);
            }
        }
        registerReceiver();
        return isOpen;
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) return;
                BlueTeethInfo blueTeethInfo = new BlueTeethInfo();
                blueTeethInfo.setName(scanDevice.getName());
                Log.d("dddsss", "onReceive: " + scanDevice.getName()+"mac："+scanDevice.getAddress());
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice bonddevice : devices) {
                    if (bonddevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        blueTeethInfo.setPei(true);
                    } else {
                        blueTeethInfo.setPei(false);
                    }
                }

                mianAdapter.addData(blueTeethInfo);
            }
        }
    };

    //注册广播搜索蓝牙
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);
    }


    /**
     * 实体
     */

    class BlueTeethInfo {
        String name;
        boolean isPei;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isPei() {
            return isPei;
        }

        public void setPei(boolean pei) {
            isPei = pei;
        }
    }

    class MianAdapter extends BaseQuickAdapter<BlueTeethInfo, BaseViewHolder> {

        public MianAdapter() {
            super(R.layout.item_mian);
        }

        @Override
        protected void convert(BaseViewHolder helper, BlueTeethInfo item) {

            helper.setText(R.id.txtName, item.getName());
            if (item.isPei) {
                helper.setText(R.id.isPeiDui, "已配对");
            } else {
                helper.setText(R.id.isPeiDui, "未配对");
            }
        }
    }
}
