package com.hdl.xw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdl.sdk.hdl_core.Config.Configuration;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Bean.AppliancesInfo;

import java.util.ArrayList;
import java.util.List;

public class AppliancesActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listString = new ArrayList<>();
    private List<AppliancesInfo> appliancesInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliances);

        listView = (ListView) findViewById(R.id.lv);
        appliancesInfos = (List<AppliancesInfo>) getIntent().getSerializableExtra("Appliances");

        for (int i = 0; i < appliancesInfos.size(); i++) {
            if (TextUtils.isEmpty(appliancesInfos.get(i).getRemarks())) {
                listString.add(appliancesInfos.get(i).getDeviceSubnetID()
                        + "-" + appliancesInfos.get(i).getDeviceDeviceID()
                        + ",类型" + appliancesInfos.get(i).getBigType()
                        + "-" + appliancesInfos.get(i).getLittleType()
                        + "回路:" + appliancesInfos.get(i).getChannelNum()
                        + " 获取备注失败或为空");
            } else {
                listString.add(appliancesInfos.get(i).getRemarks());
            }
        }

        adapter = new ArrayAdapter<String>(AppliancesActivity.this, android.R.layout.simple_list_item_1, listString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (appliancesInfos.get(position).getBigType() == Configuration.AUDIO_BIG_TYPE) {
                    //大类是音乐类则跳转到AudioActivity
                    intent.setClass(AppliancesActivity.this, AudioActivity.class);
                } else {
                    intent.setClass(AppliancesActivity.this, CtrlActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("hdl", appliancesInfos.get(position));
                intent.putExtras(bundle);
                AppliancesActivity.this.startActivity(intent);
            }
        });

    }

}
