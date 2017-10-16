package com.hdl.xw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hdl.libr.hdl_lib.CommandData;
import com.hdl.libr.hdl_lib.DeviceManager.Bean.AppliancesInfo;
import com.hdl.libr.hdl_lib.DeviceManager.Bean.DevicesData;
import com.hdl.libr.hdl_lib.DeviceManager.DeviceManager;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.DevicesInfoEvent;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.SceneInfoEvent;
import com.hdl.libr.hdl_lib.OnDevices.EventBusEvent.OnDeviceDataEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button getDevices,getScenes;
    TextView tv;
    private List<DevicesData> devicesDatas;
    private List<DevicesData> OndevicesDatas;
    private List<String> listString = new ArrayList<>() ;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DeviceManager.init(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        getDevices = (Button) findViewById(R.id.devices);
        getScenes = (Button) findViewById(R.id.scenes);
        tv= (TextView) findViewById(R.id.tv);
        adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,listString);
        ListView listView=(ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AppliancesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Appliances", (Serializable)devicesDatas.get(position).getAppliancesInfoList());
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });


        getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandData.HDLdevicesSearch(MainActivity.this);
            }
        });

        getScenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.HDLscenesSearch(MainActivity.this);
            }
        });
    }


    @Override
    protected void onDestroy() {
        //关闭Socket接收
        super.onDestroy();
        DeviceManager.release();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDevicesInfoEventMain(DevicesInfoEvent event){
        devicesDatas = event.getDesDataList();
        tv.setText("size = "+event.getDesDataList().size());
        listString.clear();
        for(int i = 0;i<devicesDatas.size();i++){
            if(TextUtils.isEmpty(devicesDatas.get(i).getRemark())){
                listString.add("暂无备注");
            }else{
                listString.add(devicesDatas.get(i).getRemark());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScenesInfoEventMain(SceneInfoEvent event){
        devicesDatas = event.getDesDataList();
        tv.setText("size = "+event.getDesDataList().size());
        listString.clear();
        for(int i = 0;i<devicesDatas.size();i++){
            if(TextUtils.isEmpty(devicesDatas.get(i).getRemark())){
                listString.add("暂无备注");
            }else{
                listString.add(devicesDatas.get(i).getRemark());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSowInfoEventMain(OnDeviceDataEvent event){
        OndevicesDatas = event.getDevicesDataList();
        for(int i=0;i<OndevicesDatas.size();i++){
            List<AppliancesInfo> appliancesInfoList = OndevicesDatas.get(i).getAppliancesInfoList();
            for(int j=0;j<appliancesInfoList.size();j++){
                Log.i("Ondevice","设备名称："+appliancesInfoList.get(j).getDeviceName()
                        +"\n子网Id = "+appliancesInfoList.get(j).getDeviceSubnetID()
                        +"\n 设备Id = "+appliancesInfoList.get(j).getDeviceDeviceID()
                        +"\n 回路号 =" +appliancesInfoList.get(j).getChannelNum()
                        +"\n 大类 =" +appliancesInfoList.get(j).getBigType()
                        +"\n 小类 =" +appliancesInfoList.get(j).getLittleType()
                        +"\n 操作码 =" +appliancesInfoList.get(j).getCtrlCommand()
                        +"\n 操作回馈码 =" +appliancesInfoList.get(j).getCtrlBackCommand()
                        +"\n 状态码 =" +appliancesInfoList.get(j).getStateCommand()
                        +"\n 状态回馈码 =" +appliancesInfoList.get(j).getStateBackCommand()

                );
            }

        }

    }



}
