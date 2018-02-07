package com.hdl.xw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.libr.hdl_lib.HDLAppliances.HDLAirCondition.Parser.AirCtrlParser;
import com.hdl.libr.hdl_lib.HDLAppliances.HDLCurtain.Parser.CurtainCtrlParser;
import com.hdl.libr.hdl_lib.HDLCommand;
import com.hdl.libr.hdl_lib.HDLDeviceManager.Bean.AppliancesInfo;
import com.hdl.libr.hdl_lib.HDLDeviceManager.EventBusEvent.AirFeedBackEvent;
import com.hdl.libr.hdl_lib.HDLDeviceManager.EventBusEvent.CurtainFeedBackEvent;
import com.hdl.libr.hdl_lib.HDLDeviceManager.EventBusEvent.DeviceStateEvent;
import com.hdl.libr.hdl_lib.HDLDeviceManager.EventBusEvent.LightFeedBackEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CtrlActivity extends AppCompatActivity {

    private Button lightBtn,curtainBtn,curtainBtn2,curtainBtn3,curtainBtn4,curtainBtn5,logicBtn,airBtn;
    private TextView lightText,curText1,curText2,airText,logicText;
    private AppliancesInfo appliancesInfo;
    private int lightState;
    private int curtainState;
    private int airState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
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
        curtainState = CurtainCtrlParser.curtainOn;
        airState = AirCtrlParser.airOn;

        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("hdl");

        //此处判断什么设备，并将其他设备控件隐藏
        //1：调光回路（灯） 2：开关回路（灯） 3：混合调光类 （灯） 4：混合开关类（灯）
        // 5：开合帘电机（窗帘）6：卷帘电机（窗帘） 7：窗帘模块 （窗帘）8：HVAC 模块(空调)
        // 9：通用空调面板(空调) 10：背景音乐模块（音乐） 11：第三方背景音乐模块（音乐）
        // 12：逻辑模块（场景） 13：全局逻辑模块（场景）

        //1、2、3、4 为灯
        //5、6、7 为窗帘
        //8、9 为空调
        //10、11 为音乐
        //12、13 为场景


        switch (appliancesInfo.getDeviceType()){
            case 1:
            case 2:
            case 3:
            case 4:
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
            case 5:
            case 6:
            case 7:
                lightBtn.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);
                airBtn.setVisibility(View.GONE);

                lightText.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                if(appliancesInfo.getDeviceType()==5){
                    curText1.setVisibility(View.GONE);
                    curtainBtn.setVisibility(View.GONE);
                }else if(appliancesInfo.getDeviceType()==6){
                    curText2.setVisibility(View.GONE);
                    curtainBtn2.setVisibility(View.GONE);
                    curtainBtn3.setVisibility(View.GONE);
                    curtainBtn4.setVisibility(View.GONE);
                    curtainBtn5.setVisibility(View.GONE);
                }
                break;
            case 8:
            case 9:
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
            case 12:
            case 13:
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

//        此方法为获取设备状态，逻辑模块、背景音乐模块没有这个api，仅支持灯光，窗帘，空调
        HDLCommand.HDLgetDeviceState(appliancesInfo);

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HDLCommand.HDLlightCtrl(appliancesInfo,lightState);
            }
        });

        curtainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //窗帘模块第二个参数 为CurtainCtrlParser.curtainOn，CurtainCtrlParser.curtainOff，CurtainCtrlParser.curtainPause其中一个
                HDLCommand.HDLcurtainCtrl(appliancesInfo, curtainState);
                if(curtainState  == CurtainCtrlParser.curtainOn){
                    curtainState = CurtainCtrlParser.curtainOff;
                }else{
                    curtainState = CurtainCtrlParser.curtainOn;
                }

            }
        });

        curtainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLcurtainCtrl(appliancesInfo, CurtainCtrlParser.curtainOn);
            }
        });

        curtainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLcurtainCtrl(appliancesInfo, CurtainCtrlParser.curtainOff);
            }
        });

        curtainBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLcurtainCtrl(appliancesInfo, CurtainCtrlParser.curtainPause);
            }
        });

        curtainBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLcurtainCtrl(appliancesInfo, 50);
            }
        });

        airBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLairCtrl(appliancesInfo, AirCtrlParser.airSwich,airState);//空调面板开
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airSwich,AirCtrlParser.airOff);//空调面板关
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.refTem,20);//制冷温度 范围0-84
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedAuto);//风速自动
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedHigh);//风速高风
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedMid);//风速中风
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedLow);//风速低风
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeRefTem);//空调模式制冷
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeHeatTem);//空调模式制热
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeVen);//空调模式通风
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeAuto);//空调模式自动
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeDehum);//空调模式抽湿
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.heatTem,28);//制热温度 范围0-84
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.autoTem,25);//自动温度 范围0-84
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.upTem,1);//上升温度 范围0-5
//                HDLCommand.HDLairCtrl(appliancesInfo,AirCtrlParser.downTem,1);//下降温度 范围0-5

                if(airState==AirCtrlParser.airOn){
                    airState = AirCtrlParser.airOff;
                }else{
                    airState = AirCtrlParser.airOn;
                }
            }
        });

        logicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLlogicCtrl(appliancesInfo);
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLightFeedBackInfoEventMain(LightFeedBackEvent event){

        if(event.getLightCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getLightCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getLightCtrlBackInfo().getChannelNum() == appliancesInfo.getChannelNum()
                ){
            int brightness = event.getLightCtrlBackInfo().getBrightness();
            lightState = brightness==100? 0:100;//如果返回100重置状态为0，反之重置状态100
            lightBtn.setText("当前亮度 = "+brightness);
        /*以下为灯光推送示例代码，可以识别哪个继电器，哪个调光灯，哪个回路，也可用作控制回馈。
        按需求调用*/
            String remarks = event.getLightCtrlBackInfo().getRemarks();//获取返回的灯光备注。如果每个灯光回路备注都唯一，可以直接通过备注判断
            String parentRemarks = event.getLightCtrlBackInfo().getParentRemarks();//获取继电器或调光灯备注。这里可以知道是哪个设备返回的
            int num = event.getLightCtrlBackInfo().getChannelNum();//获取回路号。这里可以获取到这个继电器或调光灯的回路号
            ToastUtil(parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 亮度为："+brightness);
        }



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurtainFeedBackInfoEventMain(CurtainFeedBackEvent event){
        if(event.getCurtainCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getCurtainCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getCurtainCtrlBackInfo().getNum() == appliancesInfo.getChannelNum()
                ){
            int curState = event.getCurtainCtrlBackInfo().getState();
            //窗帘模块：curState:0=停止,1=打开,2=关闭。
            //开合帘电机，卷帘电机：curState:1-100开合度。也会返回0，1，2的状态
            //建议开合帘电机，卷帘电机按停止后再读取当前状态来获取当前状态值

            String remarks = event.getCurtainCtrlBackInfo().getRemarks();
            String parentRemarks = event.getCurtainCtrlBackInfo().getParentRemarks();
            int num = event.getCurtainCtrlBackInfo().getNum();
            ToastUtil(parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 状态为："+curState);
            switch (curState){
                case 2:
                    curtainBtn.setText("窗帘关");
                    break;
                case 1:
                    curtainBtn.setText("窗帘开");
                    break;
                case 0:
                    curtainBtn.setText("窗帘暂停");
                    break;
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAirFeedBackInfoEventMain(AirFeedBackEvent event){
        if(event.getAirCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getAirCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getAirCtrlBackInfo().getAppliancesInfo().getChannelNum() == appliancesInfo.getChannelNum()
                ){
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


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceStateEventMain(DeviceStateEvent event){
        if(event.getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                ){

        }
        //这个返回的信息是当前状态的
        switch (event.getAppliancesInfo().getDeviceType()){
            case 1:
            case 2:
            case 3:
            case 4:
                if(appliancesInfo.getChannelNum()==event.getAppliancesInfo().getChannelNum()){
                    lightBtn.setText("亮度 = "+event.getAppliancesInfo().getCurState());
                }
                break;
            case 5:
            case 6:
            case 7:
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
            case 8:
            case 9:
                if(appliancesInfo.getChannelNum()==event.getAppliancesInfo().getChannelNum()){
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
                }
                break;
        }

    }


    public void ToastUtil(String text){
        Toast.makeText(CtrlActivity.this,text,Toast.LENGTH_SHORT).show();
    }

}
