package com.hdl.xw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.sdk.hdl_core.HDLAppliances.Config.HDLApConfig;
import com.hdl.sdk.hdl_core.HDLAppliances.HDLAirCondition.Parser.AirCtrlParser;
import com.hdl.sdk.hdl_core.HDLAppliances.HDLCurtain.Parser.CurtainCtrlParser;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Bean.AppliancesInfo;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Core.HDLCommand;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.AirFeedBackEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.CurtainFeedBackEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.DeviceStateEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.LightFeedBackEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.LogicFeedBackEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CtrlActivity extends AppCompatActivity {

    private Button lightBtn, curtainBtn, curtainBtn2, curtainBtn3, curtainBtn4, curtainBtn5, logicBtn, airBtn;
    private TextView lightText, curText1, curText2, airText, logicText;
    private AppliancesInfo appliancesInfo;
    private int lightState;
    private int curtainState;
    private int airState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initcurState();
        initView();


//        此方法为主动获取单一设备状态，逻辑模块、背景音乐模块没有这个api，仅支持灯光，窗帘，空调。
//        HDLCommand.getDeviceState(appliancesInfo);


        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HDLCommand.lightCtrl(appliancesInfo, lightState);
//                HDLCommand.lightCtrl(appliancesInfo,50);
            }
        });

        curtainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //窗帘模块第二个参数 为CurtainCtrlParser.curtainOn，CurtainCtrlParser.curtainOff，CurtainCtrlParser.curtainPause其中一个
                HDLCommand.curtainCtrl(appliancesInfo, curtainState);
                if (curtainState == CurtainCtrlParser.curtainOn) {
                    curtainState = CurtainCtrlParser.curtainOff;
                } else {
                    curtainState = CurtainCtrlParser.curtainOn;
                }

            }
        });

        curtainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.curtainCtrl(appliancesInfo, CurtainCtrlParser.curtainOn);
            }
        });

        curtainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.curtainCtrl(appliancesInfo, CurtainCtrlParser.curtainOff);
            }
        });

        curtainBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.curtainCtrl(appliancesInfo, CurtainCtrlParser.curtainPause);
            }
        });

        curtainBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.curtainCtrl(appliancesInfo, 50);
            }
        });

        airBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSwich, airState);//空调面板开
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSwich,AirCtrlParser.airOff);//空调面板关
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedAuto);//风速自动
                HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSpeed, AirCtrlParser.airSpeedHigh);//风速高风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedMid);//风速中风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedLow);//风速低风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeRefTem);//空调模式制冷
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeHeatTem);//空调模式制热
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeVen);//空调模式通风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeAuto);//空调模式自动
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeDehum);//空调模式抽湿
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.heatTem,28);//制热温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.autoTem,25);//自动温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.refTem, 20);//制冷温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.dehumTem,20);//抽湿温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.upTem,1);//上升温度 范围0-5
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.downTem,1);//下降温度 范围0-5


//                这里仅作为演示，将开关反重置
                if (airState == AirCtrlParser.airOn) {
                    airState = AirCtrlParser.airOff;
                } else {
                    airState = AirCtrlParser.airOn;
                }
            }
        });

        logicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.logicCtrl(appliancesInfo);
            }
        });
    }

    private void initView() {
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
        //此处判断什么设备，并将其他设备控件隐藏
        //1：调光回路（灯） 2：开关回路（灯） 3：混合调光类 （灯） 4：混合开关类（灯）
        // 5：开合帘电机（窗帘）6：卷帘电机（窗帘） 7：窗帘模块 （窗帘）
        // 8：HVAC 模块(空调) 9：通用空调面板(空调)
        // 10：背景音乐模块（音乐） 11：第三方背景音乐模块（音乐）
        // 12：逻辑模块（场景） 13：全局逻辑模块（场景）

        //1、2、3、4 为灯 TYPE_LIGHT_DIMMER、TYPE_LIGHT_RELAY、TYPE_LIGHT_MIX_DIMMER、TYPE_LIGHT_MIX_RELAY
        //5、6、7 为窗帘 TYPE_CURTAIN_GLYSTRO、TYPE_CURTAIN_ROLLER、TYPE_CURTAIN_MODULE
        //8、9 为空调 TYPE_AC_HVAC、TYPE_AC_PANEL
        //10、11 为音乐 TYPE_MUSIC_MODULE、TYPE_MUSIC_THIRD_PARTY_MODULE
        //12、13 为场景 TYPE_LOGIC_MODULE、TYPE_GLOBAL_LOGIC_MODULE


        switch (appliancesInfo.getDeviceType()) {
            case HDLApConfig.TYPE_LIGHT_DIMMER:
            case HDLApConfig.TYPE_LIGHT_RELAY:
            case HDLApConfig.TYPE_LIGHT_MIX_DIMMER:
            case HDLApConfig.TYPE_LIGHT_MIX_RELAY:
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

                //这里Demo示例获取单一回路的状态。
                int curLightState = (int) appliancesInfo.getCurState();

                lightText.setText("当前灯光亮度：" + curLightState);
                lightBtn.setText("当前灯光亮度：" + curLightState);
                if (curLightState == 100) {
                    lightState = 0;
                } else {
                    lightState = 100;
                }
                break;
            case HDLApConfig.TYPE_CURTAIN_GLYSTRO:
            case HDLApConfig.TYPE_CURTAIN_ROLLER:
            case HDLApConfig.TYPE_CURTAIN_MODULE:
                lightBtn.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);
                airBtn.setVisibility(View.GONE);

                lightText.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                if (appliancesInfo.getDeviceType() == HDLApConfig.TYPE_CURTAIN_MODULE) {
                    //窗帘模块
                    curText2.setVisibility(View.GONE);
                    curtainBtn2.setVisibility(View.GONE);
                    curtainBtn3.setVisibility(View.GONE);
                    curtainBtn4.setVisibility(View.GONE);
                    curtainBtn5.setVisibility(View.GONE);
                    String stringCurtainState = "";
                    int curCurtainState = (int) appliancesInfo.getCurState();
                    Log.i("djl", "curCurtainState = " + curCurtainState);
                    switch (curCurtainState) {
                        case 0:
                            stringCurtainState += "窗帘模块停止状态";
                            curtainState = CurtainCtrlParser.curtainOn;//初始化窗帘控制状态
                            break;
                        case 1:
                            stringCurtainState += "窗帘模块开状态";
                            curtainState = CurtainCtrlParser.curtainOff;//初始化窗帘控制状态
                            break;
                        case 2:
                            stringCurtainState += "窗帘模块关状态";
                            curtainState = CurtainCtrlParser.curtainOn;//初始化窗帘控制状态
                            break;
                        default:
                            stringCurtainState = "未获取到窗帘模块状态";
                            curtainState = CurtainCtrlParser.curtainOff;//初始化窗帘控制状态
                            break;
                    }
                    curText1.setText(stringCurtainState);
                    curtainBtn.setText(stringCurtainState);
                } else {
                    //开合帘、卷帘
                    curText1.setVisibility(View.GONE);
                    curtainBtn.setVisibility(View.GONE);

                    curText2.setText("当前窗帘状态：" + appliancesInfo.getCurState());


                }
                break;
            case HDLApConfig.TYPE_AC_HVAC:
            case HDLApConfig.TYPE_AC_PANEL:
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

                String stringACState = "";
                byte[] acCurState = appliancesInfo.getArrCurState();
                for (int index = 0; index < acCurState.length; index++) {
                    if (index == 0 && acCurState[index] == 0) {
                        stringACState += "空调已关闭";
                        //如果空调关闭状态，则无需再遍历
                        break;
                    }
                    if (index == 0 && acCurState[index] == 1) {
                        stringACState += "空调正在运行";
                    }

                    switch (index) {

                        case 1:
                            switch (acCurState[index]) {
                                case 0:
                                    stringACState += " 空调模式:制冷";
                                    break;
                                case 1:
                                    stringACState += " 空调模式:制热";
                                    break;
                                case 2:
                                    stringACState += " 空调模式:通风";
                                    break;
                                case 3:
                                    stringACState += " 空调模式:自动";
                                    break;
                                case 4:
                                    stringACState += " 空调模式:抽湿";
                                    break;
                                default:
                                    stringACState += " 未知空调模式";
                                    break;
                            }
                            break;
                        case 2:
                            switch (acCurState[1]) {
                                case 0:
                                    stringACState += " 制冷温度：" + acCurState[index];
                                    break;
                                case 1:
                                    stringACState += " 制热温度：" + acCurState[index];
                                    break;
                                case 2:
                                    stringACState += " 通风模式下，无温度显示";
                                    break;
                                case 3:
                                    stringACState += " 自动温度：" + acCurState[index];
                                    break;
                                case 4:
                                    stringACState += " 抽湿温度：" + acCurState[index];
                                    break;
                                default:
                                    stringACState += " 未知温度";
                                    break;
                            }
                            break;
                        case 3:
                            String curSpeed;
                            switch (appliancesInfo.getArrCurState()[index]) {
                                case 0:
                                    curSpeed = " 风速自动";
                                    break;
                                case 1:
                                    curSpeed = " 风速高";
                                    break;
                                case 2:
                                    curSpeed = " 风速中";
                                    break;
                                case 3:
                                    curSpeed = " 风速低";
                                    break;
                                default:
                                    curSpeed = " 未知风速";
                                    break;
                            }
                            switch (appliancesInfo.getArrCurState()[1]) {
                                case 0:
                                    stringACState += curSpeed;
                                    break;
                                case 1:
                                    stringACState += curSpeed;
                                    break;
                                case 2:
                                    stringACState += curSpeed;
                                    break;
                                case 3:
                                    stringACState += curSpeed;
                                    break;
                                case 4:
                                    stringACState += " 抽湿无风速";
                                    break;
                                default:
                                    stringACState += " 未知空调模式";
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                }

                airText.setText(stringACState);


                break;
            case HDLApConfig.TYPE_LOGIC_MODULE:
            case HDLApConfig.TYPE_GLOBAL_LOGIC_MODULE:
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
    }

    private void initcurState() {
//        lightState = 100;//初始化灯光亮度100
        curtainState = CurtainCtrlParser.curtainOff;
        airState = AirCtrlParser.airOn;//初始化空调开

        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("hdl");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLightFeedBackInfoEventMain(LightFeedBackEvent event) {

        if (event.getLightCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getLightCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getLightCtrlBackInfo().getChannelNum() == appliancesInfo.getChannelNum()
                ) {
            //        先判断是否超时
            if (!event.isSuccess()) {
                ToastUtil("灯光控制超时，请重新再试");
                lightBtn.setText("灯光控制超时，请重新再试");
                return;
            }
            int brightness = event.getLightCtrlBackInfo().getBrightness();
            lightState = brightness == 100 ? 0 : 100;//如果返回100重置状态为0，反之重置状态100
            lightBtn.setText("当前亮度 = " + brightness);
            lightText.setText("当前亮度 = " + brightness);
        /*以下为灯光推送示例代码，可以识别哪个继电器，哪个调光灯，哪个回路，也可用作控制回馈。
        按需求调用*/
            String remarks = event.getLightCtrlBackInfo().getRemarks();//获取返回的灯光备注。如果每个灯光回路备注都唯一，可以直接通过备注判断
            String parentRemarks = event.getLightCtrlBackInfo().getParentRemarks();//获取继电器或调光灯备注。这里可以知道是哪个设备返回的
            int num = event.getLightCtrlBackInfo().getChannelNum();//获取回路号。这里可以获取到这个继电器或调光灯的回路号
            ToastUtil(parentRemarks + " 的 " + remarks + " 回路号：" + num + " 返回" + " 亮度为：" + brightness);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurtainFeedBackInfoEventMain(CurtainFeedBackEvent event) {
//        先判断是否超时
        Log.i("djl", "onCurtainFeedBackInfoEventMain in");
        if (event.getCurtainCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getCurtainCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getCurtainCtrlBackInfo().getNum() == appliancesInfo.getChannelNum()
                ) {
            if (!event.isSuccess()) {
                ToastUtil("窗帘控制超时，请重新再试");
                return;
            }

            int curState = event.getCurtainCtrlBackInfo().getState();
            //窗帘模块：curState:0=停止,1=打开,2=关闭。
            //开合帘电机，卷帘电机：curState:1-100开合度。也会返回0，1，2的状态
            //建议开合帘电机，卷帘电机按停止后再读取当前状态来获取当前状态值

            String remarks = event.getCurtainCtrlBackInfo().getRemarks();
            String parentRemarks = event.getCurtainCtrlBackInfo().getParentRemarks();
            int num = event.getCurtainCtrlBackInfo().getNum();
//            ToastUtil(parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 状态为："+curState);
            Log.i("djl", parentRemarks + " 的 " + remarks + " 回路号：" + num + " 返回" + " 状态为：" + curState);
            if (event.getCurtainCtrlBackInfo().getAppliancesInfo().getDeviceType() == HDLApConfig.TYPE_CURTAIN_MODULE) {
                //判断是否为窗帘模块
                switch (curState) {
                    case CurtainCtrlParser.TYPE_STATE_CLOSE:
                        curtainBtn.setText("窗帘关");
                        curText1.setText("窗帘关");
                        Log.i("djl", "窗帘控制 ：窗帘关" + "  回路号：" + num);
                        break;
                    case CurtainCtrlParser.TYPE_STATE_OPEN:
                        curtainBtn.setText("窗帘开");
                        curText1.setText("窗帘开");
                        Log.i("djl", "窗帘控制 ：窗帘开" + "  回路号：" + num);
                        break;
                    case CurtainCtrlParser.TYPE_STATE_PAUSE:
                        curtainBtn.setText("窗帘暂停");
                        curText1.setText("窗帘暂停");
                        Log.i("djl", "窗帘控制 ：窗帘暂停" + "  回路号：" + num);
                        break;
                }
            } else {
                //开合帘或卷帘 显示百分比
                curtainBtn5.setText("窗帘开到" + curState + "%");
                curText2.setText("窗帘开到" + curState + "%");
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAirFeedBackInfoEventMain(AirFeedBackEvent event) {
        if (event.getAirCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getAirCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getAirCtrlBackInfo().getAppliancesInfo().getChannelNum() == appliancesInfo.getChannelNum()
                ) {
            //        先判断是否超时
            if (!event.isSuccess()) {
                ToastUtil("空调控制超时，请重新再试");
                return;
            }

            byte[] curState = event.getAirCtrlBackInfo().getCurState();
            switch (curState[0] & 0xFF) {
                case AirCtrlParser.airSwich:
                    switch (curState[1] & 0xFF) {
                        case AirCtrlParser.airOff:
                            airBtn.setText("空调关");
                            airText.setText("空调关");
                            ToastUtil("空调关");
                            Log.i("djl", "空调关");
                            break;
                        case AirCtrlParser.airOn:
                            airBtn.setText("空调开");
                            airText.setText("空调开");
                            ToastUtil("空调开");
                            Log.i("djl", "空调开");
                            break;
                        default:
                            break;
                    }

                    break;
                case AirCtrlParser.refTem:
                    airBtn.setText("空调制冷，温度为：" + (curState[1] & 0xFF));
                    airText.setText("空调制冷，温度为：" + (curState[1] & 0xFF));
                    ToastUtil("空调制冷，温度为：" + (curState[1] & 0xFF));
                    Log.i("djl", "空调制冷，温度为：" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.airSpeed:
                    switch (curState[1] & 0xFF) {
                        case AirCtrlParser.airSpeedAuto:
                            airBtn.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                            airText.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                            ToastUtil("空调风速，风速模式为：airSpeedAuto自动风速");
                            Log.i("djl", "空调风速，风速模式为：airSpeedAuto自动风速");
                            break;
                        case AirCtrlParser.airSpeedHigh:
                            airBtn.setText("空调风速，风速模式为：airSpeedHigh风速高");
                            airText.setText("空调风速，风速模式为：airSpeedHigh风速高");
                            ToastUtil("空调风速，风速模式为：airSpeedHigh风速高");
                            Log.i("djl", "空调风速，风速模式为：airSpeedHigh风速高");
                            break;
                        case AirCtrlParser.airSpeedMid:
                            airBtn.setText("空调风速，风速模式为：airSpeedMid风速中");
                            airText.setText("空调风速，风速模式为：airSpeedMid风速中");
                            ToastUtil("空调风速，风速模式为：airSpeedMid风速中");
                            Log.i("djl", "空调风速，风速模式为：airSpeedMid风速中");
                            break;
                        case AirCtrlParser.airSpeedLow:
                            airBtn.setText("空调风速，风速模式为：airSpeedLow风速低");
                            airText.setText("空调风速，风速模式为：airSpeedLow风速低");
                            ToastUtil("空调风速，风速模式为：airSpeedLow风速低");
                            Log.i("djl", "空调风速，风速模式为：airSpeedLow风速低");
                            break;
                        default:
                            break;
                    }
                    break;
                case AirCtrlParser.airMode:
                    switch (curState[1] & 0xFF) {
                        case AirCtrlParser.airModeRefTem:
                            airBtn.setText("空调模式，模式为：制冷");
                            airText.setText("空调模式，模式为：制冷");
                            ToastUtil("空调模式，模式为：制冷");
                            Log.i("djl", "空调模式，模式为：制冷");
                            break;
                        case AirCtrlParser.airModeHeatTem:
                            airBtn.setText("空调模式，模式为：制热");
                            airText.setText("空调模式，模式为：制热");
                            ToastUtil("空调模式，模式为：制热");
                            Log.i("djl", "空调模式，模式为：制热");
                            break;
                        case AirCtrlParser.airModeVen:
                            airBtn.setText("空调模式，模式为：通风");
                            airText.setText("空调模式，模式为：通风");
                            ToastUtil("空调模式，模式为：通风");
                            Log.i("djl", "空调模式，模式为：通风");
                            break;
                        case AirCtrlParser.airModeAuto:
                            airBtn.setText("空调模式，模式为：自动");
                            airText.setText("空调模式，模式为：自动");
                            ToastUtil("空调模式，模式为：自动");
                            Log.i("djl", "空调模式，模式为：自动");
                            break;
                        case AirCtrlParser.airModeDehum:
                            airBtn.setText("空调模式，模式为：抽湿");
                            airText.setText("空调模式，模式为：抽湿");
                            ToastUtil("空调模式，模式为：抽湿");
                            Log.i("djl", "空调模式，模式为：抽湿");
                            break;
                        default:
                            break;
                    }
                    break;
                case AirCtrlParser.heatTem:
                    airBtn.setText("空调制热，制热温度为" + (curState[1] & 0xFF));
                    airText.setText("空调制热，制热温度为" + (curState[1] & 0xFF));
                    ToastUtil("空调制热，制热温度为" + (curState[1] & 0xFF));
                    Log.i("djl", "空调制热，制热温度为" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.autoTem:
                    airBtn.setText("空调自动，自动温度为" + (curState[1] & 0xFF));
                    airText.setText("空调自动，自动温度为" + (curState[1] & 0xFF));
                    ToastUtil("空调自动，自动温度为" + (curState[1] & 0xFF));
                    Log.i("djl", "空调自动，自动温度为" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.dehumTem:
                    airBtn.setText("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    airText.setText("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    ToastUtil("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    Log.i("djl", "空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.upTem:
                    airBtn.setText("空调调温，上升温度：" + (curState[1] & 0xFF));
                    airText.setText("空调调温，上升温度：" + (curState[1] & 0xFF));
                    ToastUtil("空调调温，上升温度：" + (curState[1] & 0xFF));
                    Log.i("djl", "空调调温，上升温度：" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.downTem:
                    airBtn.setText("空调调温，下降温度：" + (curState[1] & 0xFF));
                    airText.setText("空调调温，下降温度：" + (curState[1] & 0xFF));
                    ToastUtil("空调调温，下降温度：" + (curState[1] & 0xFF));
                    Log.i("djl", "空调调温，下降温度：" + (curState[1] & 0xFF));
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogicFeedBackInfoEventMain(LogicFeedBackEvent event) {
//        先判断是否超时
        if (event.getLogicCtrlBackInfo().getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                && event.getLogicCtrlBackInfo().getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getLogicCtrlBackInfo().getAppliancesInfo().getChannelNum() == appliancesInfo.getChannelNum()
                ) {
            if (!event.isSuccess()) {
                ToastUtil("场景控制超时，请重新再试");
                return;
            }
            ToastUtil("场景控制成功");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceStateEventMain(DeviceStateEvent event) {
//        Log.i("djl","onDeviceStateEventMain in Local subid ="+appliancesInfo.getDeviceSubnetID()
//                +" Local devid = "+appliancesInfo.getDeviceDeviceID()
//                +" Rev subId = "+event.getAppliancesInfo().getDeviceSubnetID()
//                +" Rev devId ="+event.getAppliancesInfo().getDeviceDeviceID());
        if (event.getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                ) {
            //这个返回的信息是当前状态的
            Log.i("djl", "event.getAppliancesInfo().getDeviceType() = " + event.getAppliancesInfo().getDeviceType());
            switch (event.getAppliancesInfo().getDeviceType()) {
                case HDLApConfig.TYPE_LIGHT_DIMMER:
                case HDLApConfig.TYPE_LIGHT_RELAY:
                case HDLApConfig.TYPE_LIGHT_MIX_DIMMER:
                case HDLApConfig.TYPE_LIGHT_MIX_RELAY:
                    if (appliancesInfo.getChannelNum() == event.getAppliancesInfo().getChannelNum()) {
                        if (!event.isSuccess()) {
                            ToastUtil("获取灯光状态失败，请重新再试");
                            return;
                        }
                        lightBtn.setText("亮度 = " + event.getAppliancesInfo().getCurState());
                    }
                    break;
                case HDLApConfig.TYPE_CURTAIN_GLYSTRO:
                case HDLApConfig.TYPE_CURTAIN_ROLLER:
                case HDLApConfig.TYPE_CURTAIN_MODULE:
                    Log.i("djl", "Local 窗帘回路号：" + appliancesInfo.getChannelNum() + " 接收到的回路号" + event.getAppliancesInfo().getChannelNum());
                    if (appliancesInfo.getChannelNum() == event.getAppliancesInfo().getChannelNum()) {
                        if (!event.isSuccess()) {
                            ToastUtil("获取窗帘状态失败，请重新再试");
                            return;
                        }
                        //窗帘模块：curState:0=停止,1=打开,2=关闭。
                        //开合帘电机，卷帘电机：curState:1-100开合度。
                        int curState = (int) event.getAppliancesInfo().getCurState();
                        Log.i("djl", "窗帘State = " + curState);
                        if (event.getAppliancesInfo().getDeviceType() == HDLApConfig.TYPE_CURTAIN_MODULE) {//判断是否为窗帘模块,否则为开合帘或卷帘电机
                            switch (curState) {
                                case CurtainCtrlParser.curtainOff:
                                    curtainBtn.setText("窗帘关");
                                    curText1.setText("窗帘关");
                                    Log.i("djl", "窗帘状态 ：窗帘关" + "  回路号：" + event.getAppliancesInfo().getChannelNum());
                                    break;
                                case CurtainCtrlParser.curtainOn:
                                    curtainBtn.setText("窗帘开");
                                    curText1.setText("窗帘开");
                                    Log.i("djl", "窗帘状态 ：窗帘开" + "  回路号：" + event.getAppliancesInfo().getChannelNum());
                                    break;
                                case CurtainCtrlParser.curtainPause:
                                    curtainBtn.setText("窗帘暂停");
                                    curText1.setText("窗帘暂停");
                                    Log.i("djl", "窗帘状态 ：窗帘暂停" + "  回路号：" + event.getAppliancesInfo().getChannelNum());
                                    break;
                            }
                        } else {
                            curtainBtn5.setText("窗帘开到" + curState + "%");
                        }
                    }
                    break;
                case HDLApConfig.TYPE_AC_HVAC:
                case HDLApConfig.TYPE_AC_PANEL:
                    Log.i("djl", "AC Local channemNum = " + appliancesInfo.getChannelNum()
                            + " Rev num = " + event.getAppliancesInfo().getChannelNum()

                    );
                    if (appliancesInfo.getChannelNum() == event.getAppliancesInfo().getChannelNum()) {
                        if (!event.isSuccess()) {
                            ToastUtil("获取空调状态失败，请重新再试");
                            return;
                        }

                        byte[] curState = event.getAppliancesInfo().getArrCurState();
                        Log.i("djl", "curState[0] " + (curState[0] & 0xFF)
                                + " curState[1] = " + (curState[1] & 0xff)

                        );
                        switch (curState[0] & 0xFF) {
                            case AirCtrlParser.airSwich:
                                switch (curState[1] & 0xFF) {
                                    case AirCtrlParser.airOff:
                                        airBtn.setText("空调关");
                                        ToastUtil("空调关");
                                        Log.i("djl", "空调关");
                                        break;
                                    case AirCtrlParser.airOn:
                                        airBtn.setText("空调开");
                                        ToastUtil("空调开");
                                        Log.i("djl", "空调开");
                                        break;
                                    default:
                                        break;
                                }

                                break;
                            case AirCtrlParser.refTem:
                                airBtn.setText("空调制冷，温度为：" + (curState[1] & 0xFF));
                                ToastUtil("空调制冷，温度为：" + (curState[1] & 0xFF));
                                Log.i("djl", "空调制冷，温度为：" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.airSpeed:
                                switch (curState[1] & 0xFF) {
                                    case AirCtrlParser.airSpeedAuto:
                                        airBtn.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                                        ToastUtil("空调风速，风速模式为：airSpeedAuto自动风速");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedAuto自动风速");
                                        break;
                                    case AirCtrlParser.airSpeedHigh:
                                        airBtn.setText("空调风速，风速模式为：airSpeedHigh风速高");
                                        ToastUtil("空调风速，风速模式为：airSpeedHigh风速高");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedHigh风速高");
                                        break;
                                    case AirCtrlParser.airSpeedMid:
                                        airBtn.setText("空调风速，风速模式为：airSpeedMid风速中");
                                        ToastUtil("空调风速，风速模式为：airSpeedMid风速中");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedMid风速中");
                                        break;
                                    case AirCtrlParser.airSpeedLow:
                                        airBtn.setText("空调风速，风速模式为：airSpeedLow风速低");
                                        ToastUtil("空调风速，风速模式为：airSpeedLow风速低");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedLow风速低");
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case AirCtrlParser.airMode:
                                switch (curState[1] & 0xFF) {
                                    case AirCtrlParser.airModeRefTem:
                                        airBtn.setText("空调模式，模式为：制冷");
                                        ToastUtil("空调模式，模式为：制冷");
                                        Log.i("djl", "空调模式，模式为：制冷");
                                        break;
                                    case AirCtrlParser.airModeHeatTem:
                                        airBtn.setText("空调模式，模式为：制热");
                                        ToastUtil("空调模式，模式为：制热");
                                        Log.i("djl", "空调模式，模式为：制热");
                                        break;
                                    case AirCtrlParser.airModeVen:
                                        airBtn.setText("空调模式，模式为：通风");
                                        ToastUtil("空调模式，模式为：通风");
                                        Log.i("djl", "空调模式，模式为：通风");
                                        break;
                                    case AirCtrlParser.airModeAuto:
                                        airBtn.setText("空调模式，模式为：自动");
                                        ToastUtil("空调模式，模式为：自动");
                                        Log.i("djl", "空调模式，模式为：自动");
                                        break;
                                    case AirCtrlParser.airModeDehum:
                                        airBtn.setText("空调模式，模式为：抽湿");
                                        ToastUtil("空调模式，模式为：抽湿");
                                        Log.i("djl", "空调模式，模式为：抽湿");
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case AirCtrlParser.heatTem:
                                airBtn.setText("空调制热，制热温度为" + (curState[1] & 0xFF));
                                ToastUtil("空调制热，制热温度为" + (curState[1] & 0xFF));
                                Log.i("djl", "空调制热，制热温度为" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.autoTem:
                                airBtn.setText("空调自动，自动温度为" + (curState[1] & 0xFF));
                                ToastUtil("空调自动，自动温度为" + (curState[1] & 0xFF));
                                Log.i("djl", "空调自动，自动温度为" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.dehumTem:
                                airBtn.setText("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                                ToastUtil("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                                Log.i("djl", "空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.upTem:
                                airBtn.setText("空调调温，上升温度：" + (curState[1] & 0xFF));
                                ToastUtil("空调调温，上升温度：" + (curState[1] & 0xFF));
                                Log.i("djl", "空调调温，上升温度：" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.downTem:
                                airBtn.setText("空调调温，下降温度：" + (curState[1] & 0xFF));
                                ToastUtil("空调调温，下降温度：" + (curState[1] & 0xFF));
                                Log.i("djl", "空调调温，下降温度：" + (curState[1] & 0xFF));
                                break;
                        }
                    }
                    break;
            }
        }
    }


    public void ToastUtil(String text) {
        Toast.makeText(CtrlActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
