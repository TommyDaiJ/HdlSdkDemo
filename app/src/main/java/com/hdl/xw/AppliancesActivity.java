package com.hdl.xw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdl.libr.hdl_lib.CommandData;
import com.hdl.libr.hdl_lib.DeviceManager.Bean.AppliancesInfo;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.AppliancesInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        listView = (ListView) findViewById(R.id.lv);
        appliancesInfos = (List<AppliancesInfo>)getIntent().getSerializableExtra("Appliances");
        CommandData.getAppliancesRemarks(AppliancesActivity.this, appliancesInfos);
        Log.i("djl","appliancesInfos size = "+appliancesInfos.size());

        listString.add("hdl null");
        adapter=new ArrayAdapter<String>(AppliancesActivity.this,android.R.layout.simple_list_item_1,listString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(AppliancesActivity.this, CtrlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("light", (Serializable)appliancesInfos.get(position));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppliancesInfoEventMain(AppliancesInfoEvent event){
        appliancesInfos = event.getAppliancesInfos();
        listString.clear();
        for(int i =0;i<appliancesInfos.size();i++){
            if(TextUtils.isEmpty(appliancesInfos.get(i).getRemarks())){
                listString.add("暂无备注");
            }else{
                listString.add(appliancesInfos.get(i).getRemarks());
            }
        }
        adapter.notifyDataSetChanged();

    }
}
