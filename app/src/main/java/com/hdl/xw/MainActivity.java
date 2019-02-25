package com.hdl.xw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.sdk.hdl_core.HDLDeviceManger.Bean.DevicesData;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Core.HDLCommand;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.DevicesInfoEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.ThirdPartyBgmInfoEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.WarningInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button btn, btn2;
    private TextView tv;
    private EditText editText;
    private List<DevicesData> devicesDatas;
    private List<String> listString = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ProgressDialog proDia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HDLCommand.init(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.get);
        tv = (TextView) findViewById(R.id.tv);
        editText = (EditText) findViewById(R.id.edt);
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listString);
        ListView listView = (ListView) findViewById(R.id.listView1);
        proDia = new ProgressDialog(MainActivity.this);
        proDia.setTitle("正在获取数据...");
        proDia.setMessage("请耐心等待");
        proDia.onStart();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AppliancesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Appliances", (Serializable) devicesDatas.get(position).getAppliancesInfoList());
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HDLCommand.getHomeDevices(MainActivity.this);
                proDia.show();


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isIP(editText.getText().toString().trim())) {
                    HDLCommand.getRcuDevices(MainActivity.this, editText.getText().toString().trim());
                    proDia.show();
                } else {
                    Toast.makeText(MainActivity.this, "请输入正确格式Ip地址", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    protected void onDestroy() {
        //关闭Socket接收
        super.onDestroy();
        HDLCommand.release();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDevicesInfoEventMain(DevicesInfoEvent event) {
        listString.clear();
        proDia.dismiss();
        if (!event.isSuccess()) {
            Toast.makeText(MainActivity.this, "搜索超时，请重新再试", Toast.LENGTH_SHORT).show();
            tv.setText("搜索超时，请重新再试");
            return;
        }
        devicesDatas = event.getDesDataList();

        int allChannel = 0;
        for (DevicesData devicesData : devicesDatas) {
            allChannel += devicesData.getAppliancesInfoList().size();
        }
        tv.setText("总共模块数：" + devicesDatas.size() + " 总共回路数：" +allChannel);
        for (int i = 0; i < devicesDatas.size(); i++) {
            if (TextUtils.isEmpty(devicesDatas.get(i).getRemark())) {
                listString.add("暂无备注");
            } else {
                listString.add(devicesDatas.get(i).getRemark());
            }
        }
        adapter.notifyDataSetChanged();
    }


//    已废除，场景数据已经和常规设备合并。若有需求需要区分，请根据设备类型
//    12、13 为场景 TYPE_LOGIC_MODULE、TYPE_GLOBAL_LOGIC_MODULE
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSceneInfoEventMain(SceneInfoEvent event) {
//        proDia.dismiss();
//        listString.clear();
//        if (!event.isSuccess()) {
//            Toast.makeText(MainActivity.this, "搜索超时，请重新再试", Toast.LENGTH_SHORT).show();
//            tv.setText("搜索超时，请重新再试");
//            return;
//        }
//        devicesDatas = event.getDesDataList();
//        tv.setText("总共模块数 ： " + event.getDesDataList().size());
//
//        for (int i = 0; i < devicesDatas.size(); i++) {
//            if (TextUtils.isEmpty(devicesDatas.get(i).getRemark())) {
//                listString.add("暂无备注");
//            } else {
//                listString.add(devicesDatas.get(i).getRemark());
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }


    //暂未开放
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onRcuIpListEventMain(RcuIpListEvent event) {
//        for (int i = 0; i < event.getRcuIpList().size(); i++) {
//            Log.i("djl", "用户收到rcuIp" + event.getRcuIpList().get(i));
//        }
//        //调用
////        HDLCommand.getRcuDevices(String rcuIp);
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWarningEventMain(WarningInfoEvent event) {
        String warningType = event.getWarningType();
        Toast.makeText(MainActivity.this, warningType, Toast.LENGTH_SHORT).show();
    }

    public static boolean isIP(String str) {

        // 匹配 1
        // String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        // 匹配 2
        String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

        // 匹配1 和匹配2均可实现Ip判断的效果
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(str).matches();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBgmEventMain(ThirdPartyBgmInfoEvent event) {
        byte[] bgmBytes = event.getBytes();
    }
}
