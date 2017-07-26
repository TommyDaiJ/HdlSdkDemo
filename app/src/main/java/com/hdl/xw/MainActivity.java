package com.hdl.xw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hdl.libr.hdl_lib.Appliances.Light.LightCtrlBackInfo;
import com.hdl.libr.hdl_lib.CommandData;
import com.hdl.libr.hdl_lib.DeviceManager.Bean.AppliancesInfo;
import com.hdl.libr.hdl_lib.DeviceManager.Bean.DevicesData;
import com.hdl.libr.hdl_lib.DeviceManager.DeviceManager;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.DevicesInfoEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn;
    TextView tv,tv1,tv2;
    private List<DevicesData> devicesDatas;
    private List<AppliancesInfo> appliancesInfos;
    private LightCtrlBackInfo lightCtrlBackInfo;
    private List<String> listString = new ArrayList<>() ;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DeviceManager.init(this);
        btn = (Button) findViewById(R.id.btn);
        tv= (TextView) findViewById(R.id.tv);
        tv1= (TextView) findViewById(R.id.tv1);
        tv2= (TextView) findViewById(R.id.tv2);
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


//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppliancesInfo appliancesInfo = new AppliancesInfo();
//                appliancesInfo.setChannelNum(1);
//                appliancesInfo.setLittleType(0);
//                appliancesInfo.setDeviceSubnetID(201);
//                appliancesInfo.setDeviceDeviceID(21);
//                appliancesInfo.setBigType(2);
//                appliancesInfo.setCtrlCommand(Configuration.CURTAIN_CTRL_COMMAND);
//                appliancesInfo.setCtrlBackCommand(Configuration.CURTAIN_CTRL_BACK_COMMAND);
//                try {
//                    CommandData.deviceControl(MainActivity.this,
//                            appliancesInfo,
//                            1
//                    );
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for(int i=0;i<devicesDatas.size();i++){
//                    List<AppliancesInfo> appliancesInfos = devicesDatas.get(i).getAppliancesInfoList();
//                    if(appliancesInfos.get(0).getBigType()==2){
//                        try {
//                            CommandData.getAppliancesRemarks(MainActivity.this,
//                                    devicesDatas.get(i).getAppliancesInfoList()
//                            );
//                        } catch (UnknownHostException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    }
//                }
//
//          }
//        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    //这个是发送命令的例子
//                           CommandData.AddSendData(
//                                    0x0031/*这个操作码，这个是灯光对应的操作码*/,
//                                    10/*这个是发送的目标子网号，可以根据实际的目标地址来改*/,
//                                    6/*这个是发送的目标设备号，可以根据实际的目标地址来改*/,
//                                    new byte[]{1,status,0,0}, /*这里的数据格式是按键协议来填的,这个数组长度不定，根据协议来定的*/
//                                    "192.168.2.255"/*发送出去的目标地址，一般用当前网络的广播地址*/,
//                                    6000/*固定值不要改*/
//                            );
//
//
//                    CommandData.devicesSearch(MainActivity.this);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                CommandData.devicesSearch(MainActivity.this);
            }
        });
    }


    @Override
    protected void onDestroy() {
        //关闭Socket接收
        super.onDestroy();
        DeviceManager.release();
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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onAppliancesInfoEventMain(AppliancesInfoEvent event){
//        appliancesInfos = event.getAppliancesInfos();
//        String aa  = "";
//        for(int i =0;i<appliancesInfos.size();i++){
//            aa += " appliancesInfos"+i+" "+appliancesInfos.get(i).getRemarks();
//            Log.i("MainApplic","appliancesInfos"+i+" "+appliancesInfos.get(i).getRemarks());
//        }
//        tv1.setText(aa);
//
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLightFeedBackInfoEventMain(LightFeedBackEvent event){
//        lightCtrlBackInfo = event.getLightCtrlBackInfo();
//
//        tv2.setText(lightCtrlBackInfo.toString());
//
//    }


}
