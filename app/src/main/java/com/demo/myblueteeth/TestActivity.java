package com.demo.myblueteeth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author： Victory
 * @Time： 2018/9/13
 * @QQ： 949021037
 * @Explain： com.demo.myblueteeth
 */
public class TestActivity extends AppCompatActivity {

    @BindView(R.id.btnOpenBle)
    Button btnOpenBle;
    @BindView(R.id.btnGetBuleTeeth)
    Button btnGetBuleTeeth;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.rvList)
    RecyclerView rvList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEUtils bleUtils=new BLEUtils(TestActivity.this);
                if (bleUtils.openBLE()){
                    Toast.makeText(TestActivity.this,"蓝牙已打开",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
