package com.hdl.xw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hdl.libr.hdl_lib.Appliances.AirCondition.Parser.AirCtrlParser;
import com.hdl.libr.hdl_lib.Appliances.Curtain.Parser.CurtainCtrlParser;
import com.hdl.libr.hdl_lib.CommandData;
import com.hdl.libr.hdl_lib.Config.Configuration;
import com.hdl.libr.hdl_lib.DeviceManager.Bean.AppliancesInfo;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.AirFeedBackEvent;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.CurtainFeedBackEvent;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.DeviceStateEvent;
import com.hdl.libr.hdl_lib.DeviceManager.EventBusEvent.LightFeedBackEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CtrlActivity extends AppCompatActivity {

    private Button lightBtn,curtainBtn,logicBtn,airBtn;
    private AppliancesInfo appliancesInfo;
    private int lightState;
    private int curtainState;
    private int airState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl);
        lightBtn = (Button) findViewById(R.id.ctrlbtn);
        curtainBtn = (Button) findViewById(R.id.curtainbtn);
        logicBtn = (Button) findViewById(R.id.logicbtn);
        airBtn = (Button) findViewById(R.id.airbtn);

        lightState = 100;//初始化灯光亮度100
        curtainState = CurtainCtrlParser.curtainOn;//初始化窗帘开
        airState = AirCtrlParser.airOff;

        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("light");


        CommandData.getDeviceState(CtrlActivity.this, appliancesInfo);

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandData.lightCtrl(CtrlActivity.this,appliancesInfo,lightState);
            }
        });

        curtainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.curtainCtrl(CtrlActivity.this,appliancesInfo, curtainState);
            }
        });

        airBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,airState);
            }
        });

        logicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.logicCtrl(CtrlActivity.this,appliancesInfo);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //初始化EventBus
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLightFeedBackInfoEventMain(LightFeedBackEvent event){
        lightState = event.getLightCtrlBackInfo().getBrightness()==100? 0:100;//如果返回100重置状态为0，反之重置状态100
        Toast.makeText(this,"当前亮度 = "+event.getLightCtrlBackInfo().getBrightness(), Toast.LENGTH_SHORT).show();
        Log.i("ctrlLight",event.getLightCtrlBackInfo().toString());
        lightBtn.setText("当前亮度 = "+event.getLightCtrlBackInfo().getBrightness());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurtainFeedBackInfoEventMain(CurtainFeedBackEvent event){
        switch (event.getCurtainCtrlBackInfo().getState()){
            case CurtainCtrlParser.curtainOff:
                curtainState = CurtainCtrlParser.curtainOn;
                curtainBtn.setText("窗帘关");
                Toast.makeText(this,"当前窗户关", Toast.LENGTH_SHORT).show();
                break;
            case CurtainCtrlParser.curtainOn:
                curtainState = CurtainCtrlParser.curtainOff;
                curtainBtn.setText("窗帘开");
                Toast.makeText(this,"当前窗户开", Toast.LENGTH_SHORT).show();
                break;
            case CurtainCtrlParser.curtainPause:
                curtainBtn.setText("窗帘暂停");
                Toast.makeText(this,"当前窗户暂停", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAirFeedBackInfoEventMain(AirFeedBackEvent event){
        airState = event.getAirCtrlBackInfo().getIsOn()==1?0:1;
        if(event.getAirCtrlBackInfo().getIsOn()==0){
            Toast.makeText(this,"空调关", Toast.LENGTH_SHORT).show();
            Log.i("djl","空调关");
            airBtn.setText("空调关");
        }else{
            Toast.makeText(this,"空调开", Toast.LENGTH_SHORT).show();
            Log.i("djl","空调开");
            airBtn.setText("空调开");
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceStateEventMain(DeviceStateEvent event){
        //这个返回的信息是当前状态的
        switch (event.getAppliancesInfo().getBigType()){
            case Configuration.LIGTH_BIG_TYPE:
                if(appliancesInfo.getChannelNum()==event.getAppliancesInfo().getChannelNum()){
                    lightBtn.setText("亮度 = "+event.getAppliancesInfo().getCurState());
                }
                break;
            case Configuration.CURTAIN_BIG_TYPE:
                if(appliancesInfo.getChannelNum()==event.getAppliancesInfo().getChannelNum()){
                    switch ((int)event.getAppliancesInfo().getCurState()){
                        case CurtainCtrlParser.curtainOff:
                            curtainBtn.setText("窗帘关");
                            break;
                        case CurtainCtrlParser.curtainOn:
                            curtainBtn.setText("窗帘开");
                            break;
                        case CurtainCtrlParser.curtainPause:
                            curtainBtn.setText("窗帘暂停");
                            break;
                    }
                }
                break;
            case Configuration.AIR_BIG_TYPE:

                break;
        }

    }




}
