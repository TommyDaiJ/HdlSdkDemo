package com.hdl.xw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdl.libr.hdl_lib.HDLDeviceManager.Bean.AppliancesInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppliancesActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listString = new ArrayList<>() ;
    private List<AppliancesInfo> appliancesInfos = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliances);
//        if(!EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().register(this);
//        }
        listView = (ListView) findViewById(R.id.lv);
        appliancesInfos = (List<AppliancesInfo>)getIntent().getSerializableExtra("Appliances");
//        HDLCommand.getAppliancesRemarks(AppliancesActivity.this, appliancesInfos);

//        listString.add("hdl null");
        for(int i =0 ;i<appliancesInfos.size();i++){
            if(TextUtils.isEmpty(appliancesInfos.get(i).getRemarks())){
                listString.add("暂无备注");
            }else{
                listString.add(appliancesInfos.get(i).getRemarks());
            }
        }

        adapter=new ArrayAdapter<String>(AppliancesActivity.this,android.R.layout.simple_list_item_1,listString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if(appliancesInfos.get(position).getBigType()== 9){
                    intent.setClass(AppliancesActivity.this, AudioActivity.class);
                }else{
                    intent.setClass(AppliancesActivity.this, CtrlActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("hdl", (Serializable)appliancesInfos.get(position));
                intent.putExtras(bundle);
                AppliancesActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
