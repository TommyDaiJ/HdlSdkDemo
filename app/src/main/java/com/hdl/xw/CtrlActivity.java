package com.hdl.xw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    private Button lightBtn,curtainBtn,curtainBtn2,curtainBtn3,curtainBtn4,curtainBtn5,logicBtn,airBtn;
    private TextView lightText,curText1,curText2,airText,logicText;
    private AppliancesInfo appliancesInfo;
    private int lightState;

    private int airState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl);
        lightBtn = (Button) findViewById(R.id.ctrlbtn);
        curtainBtn = (Button) findViewById(R.id.curtainbtn);
        curtainBtn2 = (Button) findViewById(R.id.curtainbtn2);
        curtainBtn3 = (Button) findViewById(R.id.curtainbtn3);
        curtainBtn4 = (Button) findViewById(R.id.curtainbtn4);
        curtainBtn5 = (Button) findViewById(R.id.curtainbtn5);
        logicBtn = (Button) findViewById(R.id.logicbtn);
        airBtn = (Button) findViewById(R.id.airbtn);

        lightText = (TextView) findViewById(R.id.lightText);
        curText1 = (TextView) findViewById(R.id.curtainText1);
        curText2 = (TextView) findViewById(R.id.curtainText2);
        airText = (TextView) findViewById(R.id.airText);
        logicText = (TextView) findViewById(R.id.logicText);


        lightState = 100;//初始化灯光亮度100



        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("light");

        switch (appliancesInfo.getBigType()){
            case Configuration.LIGTH_BIG_TYPE:
                curtainBtn.setVisibility(View.GONE);
                curtainBtn2.setVisibility(View.GONE);
                curtainBtn3.setVisibility(View.GONE);
                curtainBtn4.setVisibility(View.GONE);
                curtainBtn5.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);
                airBtn.setVisibility(View.GONE);

                curText1.setVisibility(View.GONE);
                curText2.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                break;
            case Configuration.CURTAIN_BIG_TYPE:
                lightBtn.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);
                airBtn.setVisibility(View.GONE);

                lightText.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                if(appliancesInfo.getLittleType()==1){
                    curText1.setVisibility(View.GONE);
                    curtainBtn.setVisibility(View.GONE);
                }else if(appliancesInfo.getLittleType()==2){
                    curText2.setVisibility(View.GONE);
                    curtainBtn2.setVisibility(View.GONE);
                    curtainBtn3.setVisibility(View.GONE);
                    curtainBtn4.setVisibility(View.GONE);
                    curtainBtn5.setVisibility(View.GONE);
                }
                break;
            case Configuration.AIR_BIG_TYPE:
                lightBtn.setVisibility(View.GONE);
                curtainBtn.setVisibility(View.GONE);
                curtainBtn2.setVisibility(View.GONE);
                curtainBtn3.setVisibility(View.GONE);
                curtainBtn4.setVisibility(View.GONE);
                curtainBtn5.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);

                curText1.setVisibility(View.GONE);
                curText2.setVisibility(View.GONE);
                lightText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                break;
            case Configuration.LOGIC_BIG_TYPE:
                lightBtn.setVisibility(View.GONE);
                curtainBtn.setVisibility(View.GONE);
                curtainBtn2.setVisibility(View.GONE);
                curtainBtn3.setVisibility(View.GONE);
                curtainBtn4.setVisibility(View.GONE);
                curtainBtn5.setVisibility(View.GONE);
                airBtn.setVisibility(View.GONE);

                curText1.setVisibility(View.GONE);
                curText2.setVisibility(View.GONE);
                lightText.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                break;
        }

//        此方法为获取设备状态，逻辑模块没有这个api，仅支持灯光，窗帘，空调
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
                //窗帘模块第二个参数 为CurtainCtrlParser.curtainOn，CurtainCtrlParser.curtainOff，CurtainCtrlParser.curtainPause其中一个
                CommandData.curtainCtrl(CtrlActivity.this,appliancesInfo, CurtainCtrlParser.curtainOn);
            }
        });

        curtainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.curtainCtrl(CtrlActivity.this,appliancesInfo, CurtainCtrlParser.curtainOn);
            }
        });

        curtainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.curtainCtrl(CtrlActivity.this,appliancesInfo, CurtainCtrlParser.curtainOff);
            }
        });

        curtainBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.curtainCtrl(CtrlActivity.this,appliancesInfo, CurtainCtrlParser.curtainPause);
            }
        });

        curtainBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.curtainCtrl(CtrlActivity.this,appliancesInfo, 50);
            }
        });

        airBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airSwich,AirCtrlParser.airOn);//空调面板开
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airSwich,AirCtrlParser.airOff);//空调面板关
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.refTem,20);//制冷温度 范围0-84
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedAuto);//风速自动
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedHigh);//风速高风
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedMid);//风速中风
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedLow);//风速低风
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeRefTem);//空调模式制冷
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeHeatTem);//空调模式制热
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeVen);//空调模式通风
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeAuto);//空调模式自动
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeDehum);//空调模式抽湿
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.heatTem,28);//制热温度 范围0-84
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.autoTem,25);//自动温度 范围0-84
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.upTem,1);//上升温度 范围0-5
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.downTem,1);//下降温度 范围0-5
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
        /**
         * 控制回馈与推送都是通过此方法返回。
         */
//        此处代码不能识别哪个灯光返回
        int brightness = event.getLightCtrlBackInfo().getBrightness();
        lightState = brightness==100? 0:100;//如果返回100重置状态为0，反之重置状态100
        lightBtn.setText("当前亮度 = "+brightness);

        /*以下为灯光推送示例代码，可以识别哪个继电器，哪个调光灯，哪个回路，也可用作控制回馈。
        按需求调用*/
        String remarks = event.getLightCtrlBackInfo().getRemarks();//获取返回的灯光备注。如果每个灯光回路备注都唯一，可以直接通过备注判断
        String parentRemarks = event.getLightCtrlBackInfo().getParentRemarks();//获取继电器或调光灯备注。这里可以知道是哪个设备返回的
        int num = event.getLightCtrlBackInfo().getChannelNum();//获取回路号。这里可以获取到这个继电器或调光灯的回路号
        ToastUtil(parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 亮度为："+brightness);
        /**
         * 如果备注不能满足需求，则可通过子网id和设备id查找。子网id，设备id共同确定唯一设备。
         */



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurtainFeedBackInfoEventMain(CurtainFeedBackEvent event){
        int curState = event.getCurtainCtrlBackInfo().getState();
        //窗帘模块：curState:0=停止,1=打开,2=关闭。
        //开合帘电机，卷帘电机：curState:1-100开合度。也会返回0，1，2的状态
        //建议开合帘电机，卷帘电机按停止后再读取当前状态来获取当前状态值

        String remarks = event.getCurtainCtrlBackInfo().getRemarks();
        String parentRemarks = event.getCurtainCtrlBackInfo().getParentRemarks();
        int num = event.getCurtainCtrlBackInfo().getNum();
        ToastUtil(parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 状态为："+curState);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAirFeedBackInfoEventMain(AirFeedBackEvent event){
//        空调面板一般只有一个
        byte[] curState = event.getAirCtrlBackInfo().getCurState();
        switch (curState[0]& 0xFF){
            case AirCtrlParser.airSwich:
                switch (curState[1]& 0xFF){
                    case AirCtrlParser.airOff:
                        airBtn.setText("空调关");
                        ToastUtil("空调关");
                        break;
                    case AirCtrlParser.airOn:
                        airBtn.setText("空调开");
                        ToastUtil("空调开");
                        break;
                    default:
                        break;
                }

                break;
            case AirCtrlParser.refTem:
                airBtn.setText("空调制冷，温度为："+(curState[1]& 0xFF));
                ToastUtil("空调制冷，温度为："+(curState[1]& 0xFF));
                break;
            case AirCtrlParser.airSpeed :
                switch (curState[1]& 0xFF){
                    case AirCtrlParser.airSpeedAuto:
                        airBtn.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                        ToastUtil("空调风速，风速模式为：airSpeedAuto自动风速");
                        break;
                    case AirCtrlParser.airSpeedHigh:
                        airBtn.setText("空调风速，风速模式为：airSpeedHigh风速高");
                        ToastUtil("空调风速，风速模式为：airSpeedHigh风速高");
                        break;
                    case AirCtrlParser.airSpeedMid:
                        airBtn.setText("空调风速，风速模式为：airSpeedMid风速中");
                        ToastUtil("空调风速，风速模式为：airSpeedMid风速中");
                        break;
                    case AirCtrlParser.airSpeedLow:
                        airBtn.setText("空调风速，风速模式为：airSpeedLow风速低");
                        ToastUtil("空调风速，风速模式为：airSpeedLow风速低");
                        break;
                    default:
                        break;
                }
                break;
            case AirCtrlParser.airMode:
                switch (curState[1]& 0xFF){
                    case AirCtrlParser.airModeRefTem:
                        airBtn.setText("空调模式，模式为：制冷");
                        ToastUtil("空调模式，模式为：制冷");
                        break;
                    case AirCtrlParser.airModeHeatTem:
                        airBtn.setText("空调模式，模式为：制热");
                        ToastUtil("空调模式，模式为：制热");
                        break;
                    case AirCtrlParser.airModeVen:
                        airBtn.setText("空调模式，模式为：通风");
                        ToastUtil("空调模式，模式为：通风");
                        break;
                    case AirCtrlParser.airModeAuto:
                        airBtn.setText("空调模式，模式为：自动");
                        ToastUtil("空调模式，模式为：自动");
                        break;
                    case AirCtrlParser.airModeDehum:
                        airBtn.setText("空调模式，模式为：抽湿");
                        ToastUtil("空调模式，模式为：抽湿");
                        break;
                    default:
                        break;
                }
                break;
            case AirCtrlParser.heatTem:
                airBtn.setText("空调制热，制热温度为"+(curState[1]& 0xFF));
                ToastUtil("空调制热，制热温度为"+(curState[1]& 0xFF));
                break;
            case AirCtrlParser.autoTem:
                airBtn.setText("空调自动，自动温度为"+(curState[1]& 0xFF));
                ToastUtil("空调自动，自动温度为"+(curState[1]& 0xFF));
                break;
            case AirCtrlParser.upTem:
                airBtn.setText("空调调温，上升温度："+(curState[1]& 0xFF));
                ToastUtil("空调调温，上升温度："+(curState[1]& 0xFF));
                break;
            case AirCtrlParser.downTem:
                airBtn.setText("空调调温，下降温度："+(curState[1]& 0xFF));
                ToastUtil("空调调温，下降温度："+(curState[1]& 0xFF));
                break;
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

                    //窗帘模块：curState:0=停止,1=打开,2=关闭。
                    //开合帘电机，卷帘电机：curState:1-100开合度。
                    int curState = (int)event.getAppliancesInfo().getCurState();
                    if(event.getAppliancesInfo().getLittleType()==2){//判断是否为窗帘模块，LittleType为2是窗帘模块，否则为开合帘或卷帘电机
                        switch (curState){
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
                    }else{
                        curtainBtn5.setText("窗帘开到"+curState+"%");
                    }
                }
                break;
            case Configuration.AIR_BIG_TYPE:
                byte[] curState = event.getAppliancesInfo().getArrCurState();
                switch (curState[0]& 0xFF){
                    case AirCtrlParser.airSwich:
                        switch (curState[1]& 0xFF){
                            case AirCtrlParser.airOff:
                                airBtn.setText("空调关");
                                ToastUtil("空调关");
                                break;
                            case AirCtrlParser.airOn:
                                airBtn.setText("空调开");
                                ToastUtil("空调开");
                                break;
                            default:
                                break;
                        }

                        break;
                    case AirCtrlParser.refTem:
                        airBtn.setText("空调制冷，温度为："+(curState[1]& 0xFF));
                        ToastUtil("空调制冷，温度为："+(curState[1]& 0xFF));
                        break;
                    case AirCtrlParser.airSpeed :
                        switch (curState[1]& 0xFF){
                            case AirCtrlParser.airSpeedAuto:
                                airBtn.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                                ToastUtil("空调风速，风速模式为：airSpeedAuto自动风速");
                                break;
                            case AirCtrlParser.airSpeedHigh:
                                airBtn.setText("空调风速，风速模式为：airSpeedHigh风速高");
                                ToastUtil("空调风速，风速模式为：airSpeedHigh风速高");
                                break;
                            case AirCtrlParser.airSpeedMid:
                                airBtn.setText("空调风速，风速模式为：airSpeedMid风速中");
                                ToastUtil("空调风速，风速模式为：airSpeedMid风速中");
                                break;
                            case AirCtrlParser.airSpeedLow:
                                airBtn.setText("空调风速，风速模式为：airSpeedLow风速低");
                                ToastUtil("空调风速，风速模式为：airSpeedLow风速低");
                                break;
                            default:
                                break;
                        }
                        break;
                    case AirCtrlParser.airMode:
                        switch (curState[1]& 0xFF){
                            case AirCtrlParser.airModeRefTem:
                                airBtn.setText("空调模式，模式为：制冷");
                                ToastUtil("空调模式，模式为：制冷");
                                break;
                            case AirCtrlParser.airModeHeatTem:
                                airBtn.setText("空调模式，模式为：制热");
                                ToastUtil("空调模式，模式为：制热");
                                break;
                            case AirCtrlParser.airModeVen:
                                airBtn.setText("空调模式，模式为：通风");
                                ToastUtil("空调模式，模式为：通风");
                                break;
                            case AirCtrlParser.airModeAuto:
                                airBtn.setText("空调模式，模式为：自动");
                                ToastUtil("空调模式，模式为：自动");
                                break;
                            case AirCtrlParser.airModeDehum:
                                airBtn.setText("空调模式，模式为：抽湿");
                                ToastUtil("空调模式，模式为：抽湿");
                                break;
                            default:
                                break;
                        }
                        break;
                    case AirCtrlParser.heatTem:
                        airBtn.setText("空调制热，制热温度为"+(curState[1]& 0xFF));
                        ToastUtil("空调制热，制热温度为"+(curState[1]& 0xFF));
                        break;
                    case AirCtrlParser.autoTem:
                        airBtn.setText("空调自动，自动温度为"+(curState[1]& 0xFF));
                        ToastUtil("空调自动，自动温度为"+(curState[1]& 0xFF));
                        break;
                    case AirCtrlParser.upTem:
                        airBtn.setText("空调调温，上升温度："+(curState[1]& 0xFF));
                        ToastUtil("空调调温，上升温度："+(curState[1]& 0xFF));
                        break;
                    case AirCtrlParser.downTem:
                        airBtn.setText("空调调温，下降温度："+(curState[1]& 0xFF));
                        ToastUtil("空调调温，下降温度："+(curState[1]& 0xFF));
                        break;
                }
                break;
        }

    }


    private void ToastUtil(String text){
        Toast.makeText(CtrlActivity.this,text,Toast.LENGTH_SHORT).show();
    }

}
