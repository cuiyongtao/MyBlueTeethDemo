package com.demo.myblueteeth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author： Victory
 * @Time： 2018/9/11
 * @QQ： 949021037
 * @Explain： com.demo.myblueteeth
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnOpenBle)
    Button btnOpenBle;
    @BindView(R.id.btnGetBuleTeeth)
    Button btnGetBuleTeeth;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.rvList)
    RecyclerView rvList;

    private MainAdapter mainAdapter;
    private List<BLEInfo> mList;
    BluetoothAdapter bluetoothAdapter;
    private UUID uuid;

    //蓝牙socket对象
    private BluetoothSocket mmSocket;
    //打印的输出流
    private static OutputStream outputStream = null;
    private final int exceptionCod = 100;
    //打印的内容
    private String mPrintContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        rvList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        mainAdapter = new MainAdapter();
        mList = new ArrayList<>();
        mainAdapter.setNewData(mList);
        rvList.setAdapter(mainAdapter);
        mPrintContent = "擦撒范德萨立法精神独立房间奥斯丁放假啊按附件哦都十分骄傲罗斯福lads附近";
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        mainAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                setPRint(position);
            }
        });
    }

    @OnClick({R.id.btnOpenBle, R.id.btnGetBuleTeeth, R.id.btnPrint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOpenBle:
                openBle();
                break;
            case R.id.btnGetBuleTeeth:
                if (bluetoothAdapter != null) {
                    registerReceiver();
                }
                break;
            case R.id.btnPrint:
                break;
        }
    }

    /**
     * 打开蓝牙
     */
    private void openBle() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 0x123);
            } else {
                bluetoothAdapter.disable();
            }
        }
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
        registerReceiver(mReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 通过广播搜索蓝牙设备
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 把搜索的设置添加到集合中
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //已经匹配的设备
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    addBluetoothDevice(device);
                    //没有匹配的设备
                } else {
                    addBluetoothDevice(device);
                }
                mainAdapter.notifyDataSetChanged();
                //搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
        }

        /**
         * 添加数据
         * @param device 蓝牙设置对象
         */
        private void addBluetoothDevice(BluetoothDevice device) {
            for (int i = 0; i < mList.size(); i++) {
                if (device.getAddress().equals(mList.get(i).getName())) {
                    mList.remove(i);
                }
            }
            if (device.getBondState() == BluetoothDevice.BOND_BONDED && device.getBluetoothClass().getDeviceClass() == 1664) {
                mList.add(0, new BLEInfo(device));
            } else {
                mList.add(new BLEInfo(device));
            }
        }
    };


    private void setPRint(int position) {
        try {
            BLEInfo bleInfo = mainAdapter.getData().get(position);
            //如果已经连接并且是打印机
            if (bleInfo.isConnect && bleInfo.getType() == 1664) {
                if (bluetoothAdapter.isEnabled()) {
                    new ConnectThread(bluetoothAdapter.getRemoteDevice(bleInfo.address)).start();
//                    progressDialog = ProgressDialog.show(MainActivity.this, "提示", "正在打印...", true);
                } else {
                    Toast.makeText(MainActivity.this, "蓝牙没有打开", Toast.LENGTH_SHORT).show();
                }
                //没有连接
            } else {
                //是打印机
                if (bleInfo.getType() == 1664) {
                    setConnect(bluetoothAdapter.getRemoteDevice(bleInfo.address), position);
                    //不是打印机
                } else {
                    Toast.makeText(MainActivity.this, "该设备不是打印机", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接为客户端
     */
    private class ConnectThread extends Thread {
        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //取消的发现,因为它将减缓连接
            bluetoothAdapter.cancelDiscovery();
            try {
                //连接socket
                mmSocket.connect();
                //连接成功获取输出流
//                PrintUtils.outputStream=mmSocket.getOutputStream();
                PrintUtils.setOutputStream(mmSocket.getOutputStream());
                PrintUtils.selectCommand(PrintUtils.RESET);
                PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
                PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
                PrintUtils.printText("美食餐厅\n\n");
                PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
                PrintUtils.printText("桌号：1号桌\n\n");
                PrintUtils.selectCommand(PrintUtils.NORMAL);
                PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
                PrintUtils.printText(PrintUtils.printTwoData("订单编号", "201507161515\n"));
                PrintUtils.printText(PrintUtils.printTwoData("点菜时间", "2016-02-16 10:46\n"));
                PrintUtils.printText(PrintUtils.printTwoData("上菜时间", "2016-02-16 11:46\n"));
                PrintUtils.printText(PrintUtils.printTwoData("人数：2人", "收银员：张三\n"));

                PrintUtils.printText("--------------------------------\n");
                PrintUtils.selectCommand(PrintUtils.BOLD);
                PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
                PrintUtils.printText("--------------------------------\n");
                PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
                PrintUtils.printText(PrintUtils.printThreeData("面", "1", "0.00\n"));
                PrintUtils.printText(PrintUtils.printThreeData("米饭", "1", "6.00\n"));
                PrintUtils.printText(PrintUtils.printThreeData("铁板烧", "1", "26.00\n"));
                PrintUtils.printText(PrintUtils.printThreeData("一个测试", "1", "226.00\n"));
                PrintUtils.printText(PrintUtils.printThreeData("牛肉面啊啊", "1", "2226.00\n"));
                PrintUtils.printText(PrintUtils.printThreeData("牛肉面啊啊啊牛肉面啊啊啊", "888", "98886.00\n"));

                PrintUtils.printText("--------------------------------\n");
                PrintUtils.printText(PrintUtils.printTwoData("合计", "53.50\n"));
                PrintUtils.printText(PrintUtils.printTwoData("抹零", "3.50\n"));
                PrintUtils.printText("--------------------------------\n");
                PrintUtils.printText(PrintUtils.printTwoData("应收", "50.00\n"));
                PrintUtils.printText("--------------------------------\n");

                PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
                PrintUtils.printText("备注：不要辣、不要香菜");
                PrintUtils.printText("\n\n\n\n\n");
                send(mPrintContent);
            } catch (Exception connectException) {
                Log.e("test", "连接失败");
                connectException.printStackTrace();
                //异常时发消息更新UI
                Message msg = new Message();
                msg.what = exceptionCod;
                // 向Handler发送消息,更新UI
                handler.sendMessage(msg);
                try {
                    mmSocket.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
        }
    }



    /**
     * 匹配设备
     *
     * @param device 设备
     */
    private void setConnect(BluetoothDevice device, int position) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
            mList.get(position).setConnect(true);
            mainAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送数据
     */
    public void send(String sendData) {
        try {
            byte[] data = sendData.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(exceptionCod); // 向Handler发送消息,更新UI

        }
    }


    //在打印异常时更新ui
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == exceptionCod) {
                Toast.makeText(MainActivity.this, "打印发送失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
