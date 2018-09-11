package com.demo.myblueteeth;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @Author： Victory
 * @Time： 2018/9/11
 * @QQ： 949021037
 * @Explain： com.demo.myblueteeth
 */
public class MainAdapter extends BaseQuickAdapter<BLEInfo, BaseViewHolder> {
    MainAdapter() {
        super(R.layout.item_mian);
    }

    @Override
    protected void convert(BaseViewHolder helper, BLEInfo item) {
        helper.setText(R.id.name, item.getName());
        helper.setText(R.id.address, item.getAddress());
        helper.setText(R.id.start, item.isConnect ? "已连接" : "未连接");
        View view = helper.getView(R.id.icon);
        view.setBackgroundResource(item.getTypeIcon());

        helper.addOnClickListener(R.id.start);
    }
}
