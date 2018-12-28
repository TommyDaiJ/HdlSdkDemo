package com.hdl.xw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.sdk.hdl_core.HDLAppliances.Config.HDLApConfig;
import com.hdl.sdk.hdl_core.HDLAppliances.HDLAirCondition.Parser.AirCtrlParser;
import com.hdl.sdk.hdl_core.HDLAppliances.HDLCurtain.Parser.CurtainCtrlParser;
import com.hdl.sdk.hdl_core.HDLAppliances.HDLSensor.SensorStateBackInfo;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Bean.AppliancesInfo;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Bean.DeviceStateBean;
import com.hdl.sdk.hdl_core.HDLDeviceManger.Core.HDLCommand;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.AirFeedBackEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.CurtainFeedBackEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.DeviceStateEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.LightFeedBackEvent;
import com.hdl.sdk.hdl_core.HDLDeviceManger.EventBusEvent.LogicFeedBackEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class CtrlActivity extends AppCompatActivity {

    private Button lightBtn, curtainBtn, curtainBtn2, curtainBtn3, curtainBtn4, curtainBtn5,
            logicBtn, airBtnSwitch, airBtnMode, airBtnTemp, airBtnSpeed, sensorBtn;
    private TextView lightText, curText1, curText2, airText, logicText, sensorText;
    private EditText airTempEd;
    private LinearLayout airDisplay;
    private AppliancesInfo appliancesInfo;
    private int lightState;
    private int curtainState;

    private int airSwitchState;//Demo仅以此作为演示，实际请根据需求开发设计
    private int airModeState;
    private int airTempState;
    private int airSpeedState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


//        此方法为主动获取单一设备状态，逻辑模块、背景音乐模块没有这个api，仅支持灯光，窗帘，空调。一般不需要用到。
//        HDLCommand.getDeviceState(appliancesInfo);


        initcurState();
        initView();

        initClickOnEvent();



    }

    private void initView() {
        lightBtn = (Button) findViewById(R.id.ctrlbtn);
        curtainBtn = (Button)findViewById(R.id.curtainbtn);
        curtainBtn2 =(Button) findViewById(R.id.curtainbtn2);
        curtainBtn3 = (Button)findViewById(R.id.curtainbtn3);
        curtainBtn4 =(Button) findViewById(R.id.curtainbtn4);
        curtainBtn5 =(Button) findViewById(R.id.curtainbtn5);
        logicBtn = (Button)findViewById(R.id.logicbtn);
        sensorBtn = (Button)findViewById(R.id.sensorbtn);

        airDisplay = (LinearLayout) findViewById(R.id.air);
        airBtnSwitch = (Button) findViewById(R.id.airbtn_switch);
        airBtnMode = (Button) findViewById(R.id.airbtn_mode);
        airBtnSpeed = (Button) findViewById(R.id.airbtn_speed);
        airBtnTemp = (Button) findViewById(R.id.airbtn_tempBtn);
        airTempEd = (EditText) findViewById(R.id.airet_tempet);

        lightText = (TextView) findViewById(R.id.lightText);
        curText1 = (TextView) findViewById(R.id.curtainText1);
        curText2 = (TextView) findViewById(R.id.curtainText2);
        airText = (TextView) findViewById(R.id.airText);
        logicText = (TextView) findViewById(R.id.logicText);
        sensorText = (TextView) findViewById(R.id.sensorText);
        //此处判断什么设备，并将其他设备控件隐藏
        //1：调光回路（灯） 2：开关回路（灯） 3：混合调光类 （灯） 4：混合开关类（灯）
        // 5：开合帘电机（窗帘）6：卷帘电机（窗帘） 7：窗帘模块 （窗帘）
        // 8：HVAC 模块(空调) 9：通用空调面板(空调)
        // 10：背景音乐模块（音乐） 11：第三方背景音乐模块（音乐）
        // 12：逻辑模块（场景） 13：全局逻辑模块（场景）

        //101、102、103、104 为灯 TYPE_LIGHT_DIMMER、TYPE_LIGHT_RELAY、TYPE_LIGHT_MIX_DIMMER、TYPE_LIGHT_MIX_RELAY
        //201、202、203 为窗帘 TYPE_CURTAIN_GLYSTRO、TYPE_CURTAIN_ROLLER、TYPE_CURTAIN_MODULE
        //301、304 为空调 TYPE_AC_HVAC、TYPE_AC_PANEL
        //501、502 为场景 TYPE_LOGIC_MODULE、TYPE_GLOBAL_LOGIC_MODULE
        //601-625 为传感器

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
                airDisplay.setVisibility(View.GONE);


                curText1.setVisibility(View.GONE);
                curText2.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);

                sensorBtn.setVisibility(View.GONE);
                sensorText.setVisibility(View.GONE);


                if (appliancesInfo.getCurState() != null) {
                    int curLightState = (int) appliancesInfo.getCurState();

                    lightText.setText("当前灯光亮度：" + curLightState);
                    lightBtn.setText("当前灯光亮度：" + curLightState);
                    if (curLightState == 100) {
                        lightState = 0;
                    } else {
                        lightState = 100;
                    }
                } else {
                    lightText.setText("未获取到灯光亮度");
                    lightBtn.setText("未获取到灯光亮度");
                }


                break;
            case HDLApConfig.TYPE_CURTAIN_GLYSTRO:
            case HDLApConfig.TYPE_CURTAIN_ROLLER:
            case HDLApConfig.TYPE_CURTAIN_MODULE:
                lightBtn.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);
                airDisplay.setVisibility(View.GONE);

                lightText.setVisibility(View.GONE);
                airText.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                sensorBtn.setVisibility(View.GONE);
                sensorText.setVisibility(View.GONE);
                if (appliancesInfo.getDeviceType() == HDLApConfig.TYPE_CURTAIN_MODULE) {
                    //窗帘模块
                    curText2.setVisibility(View.GONE);
                    curtainBtn2.setVisibility(View.GONE);
                    curtainBtn3.setVisibility(View.GONE);
                    curtainBtn4.setVisibility(View.GONE);
                    curtainBtn5.setVisibility(View.GONE);

                    if (appliancesInfo.getCurState() != null) {
                        String stringCurtainState = "";
                        int curCurtainState = (int) appliancesInfo.getCurState();
                        Log.i("djl", "curCurtainState = " + curCurtainState);
                        switch (curCurtainState) {
                            case CurtainCtrlParser.TYPE_STATE_PAUSE:
                                stringCurtainState += "窗帘模块停止状态";
                                curtainState = CurtainCtrlParser.curtainOn;//初始化窗帘控制状态
                                break;
                            case CurtainCtrlParser.TYPE_STATE_OPEN:
                                stringCurtainState += "窗帘模块开状态";
                                curtainState = CurtainCtrlParser.curtainOff;//初始化窗帘控制状态
                                break;
                            case CurtainCtrlParser.TYPE_STATE_CLOSE:
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
                        curText1.setText("未获取到窗帘模块状态");
                        curtainBtn.setText("未获取到窗帘模块状态");
                    }

                } else {
                    //开合帘、卷帘
                    curText1.setVisibility(View.GONE);
                    curtainBtn.setVisibility(View.GONE);

                    if (appliancesInfo.getCurState() != null) {
                        curText2.setText("当前窗帘状态：" + appliancesInfo.getCurState());
                    } else {
                        curText2.setText("未获取到窗帘模块状态");
                    }


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
                sensorBtn.setVisibility(View.GONE);
                sensorText.setVisibility(View.GONE);


                if (appliancesInfo.getArrCurState() != null) {
                    String stringACState = "";
                    byte[] acCurState = appliancesInfo.getArrCurState();
                    for (int index = 0; index < acCurState.length; index++) {
                        if (index == 0 && acCurState[index] == 0) {
                            airSwitchState = 0;
                            stringACState += "空调已关闭";
                            //如果空调关闭状态，则无需再遍历
                            break;
                        }
                        if (index == 0 && acCurState[index] == 1) {
                            airSwitchState = 1;
                            stringACState += "空调正在运行";
                        }

                        switch (index) {

                            case 1:
                                switch (acCurState[index]) {
                                    case 0:
                                        airModeState = 0;
                                        stringACState += " 空调模式:制冷";
                                        break;
                                    case 1:
                                        airModeState = 1;
                                        stringACState += " 空调模式:制热";
                                        break;
                                    case 2:
                                        airModeState = 2;
                                        stringACState += " 空调模式:通风";
                                        break;
                                    case 3:
                                        airModeState = 3;
                                        stringACState += " 空调模式:自动";
                                        break;
                                    case 4:
                                        airModeState = 4;
                                        stringACState += " 空调模式:抽湿";
                                        break;
                                    default:
                                        airModeState = -1;
                                        stringACState += " 未知空调模式";
                                        break;
                                }
                                break;
                            case 2:
                                switch (acCurState[1]) {
                                    case 0:
                                        airTempState = acCurState[index] & 0xff;
                                        stringACState += " 制冷温度：" + (acCurState[index] & 0xff);
                                        break;
                                    case 1:
                                        airTempState = acCurState[index] & 0xff;
                                        stringACState += " 制热温度：" + (acCurState[index] & 0xff);
                                        break;
                                    case 2:
                                        airTempState = -1;
                                        stringACState += " 通风无温度显示";
                                        break;
                                    case 3:
                                        airTempState = acCurState[index] & 0xff;
                                        stringACState += " 自动温度：" + (acCurState[index] & 0xff);
                                        break;
                                    case 4:
                                        airTempState = acCurState[index] & 0xff;
                                        stringACState += " 抽湿温度：" + (acCurState[index] & 0xff);
                                        break;
                                    default:
                                        airTempState = -2;
                                        stringACState += " 未知温度";
                                        break;
                                }
                                break;
                            case 3:
                                String curSpeed;
                                switch (appliancesInfo.getArrCurState()[index]) {
                                    case 0:
                                        airSpeedState = 0;
                                        curSpeed = " 风速自动";
                                        break;
                                    case 1:
                                        airSpeedState = 1;
                                        curSpeed = " 风速高";
                                        break;
                                    case 2:
                                        airSpeedState = 2;
                                        curSpeed = " 风速中";
                                        break;
                                    case 3:
                                        airSpeedState = 3;
                                        curSpeed = " 风速低";
                                        break;
                                    default:
                                        airSpeedState = -1;
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
                } else {
                    airText.setText("未获取到空调设备状态");
                }


                break;
            case HDLApConfig.TYPE_LOGIC_MODULE:
            case HDLApConfig.TYPE_GLOBAL_LOGIC_MODULE:
                lightBtn.setVisibility(View.GONE);
                curtainBtn.setVisibility(View.GONE);
                curtainBtn2.setVisibility(View.GONE);
                curtainBtn3.setVisibility(View.GONE);
                curtainBtn4.setVisibility(View.GONE);
                curtainBtn5.setVisibility(View.GONE);
                airDisplay.setVisibility(View.GONE);

                curText1.setVisibility(View.GONE);
                curText2.setVisibility(View.GONE);
                lightText.setVisibility(View.GONE);
                sensorBtn.setVisibility(View.GONE);
                sensorText.setVisibility(View.GONE);

                break;
            case HDLApConfig.TYPE_SENSOR_DRY_CONTACT:
            case HDLApConfig.TYPE_SENSOR_MOVEMENT_DETECTOR:
            case HDLApConfig.TYPE_SENSOR_TEMP:
            case HDLApConfig.TYPE_SENSOR_HUMIDITY:
            case HDLApConfig.TYPE_SENSOR_ILLUMINACE:
            case HDLApConfig.TYPE_SENSOR_VOC:
            case HDLApConfig.TYPE_SENSOR_PM_2_POINT_5:
            case HDLApConfig.TYPE_SENSOR_C02:
            case HDLApConfig.TYPE_SENSOR_LPG:
            case HDLApConfig.TYPE_SENSOR_CO_H2:
            case HDLApConfig.TYPE_SENSOR_CH4:
            case HDLApConfig.TYPE_SENSOR_SMOG:
            case HDLApConfig.TYPE_SENSOR_WIND_SPEED:
            case HDLApConfig.TYPE_SENSOR_WIND_PRESSURE:
            case HDLApConfig.TYPE_SENSOR_LIQUID_FLOW:
            case HDLApConfig.TYPE_SENSOR_LIQUID_PRESSURE:
            case HDLApConfig.TYPE_SENSOR_LIQUID_DEPTH:
            case HDLApConfig.TYPE_SENSOR_RAIN_FALL:
            case HDLApConfig.TYPE_SENSOR_WEIGHT:
            case HDLApConfig.TYPE_SENSOR_HEIGHT_LENGTH:
            case HDLApConfig.TYPE_SENSOR_OBJECT_SPEED:
            case HDLApConfig.TYPE_SENSOR_SHAKE:
            case HDLApConfig.TYPE_SENSOR_VOLTAGE:
            case HDLApConfig.TYPE_SENSOR_ELECTRICITY:
            case HDLApConfig.TYPE_SENSOR_POWER:
                lightBtn.setVisibility(View.GONE);
                curtainBtn.setVisibility(View.GONE);
                curtainBtn2.setVisibility(View.GONE);
                curtainBtn3.setVisibility(View.GONE);
                curtainBtn4.setVisibility(View.GONE);
                curtainBtn5.setVisibility(View.GONE);
                airDisplay.setVisibility(View.GONE);

                curText1.setVisibility(View.GONE);
                curText2.setVisibility(View.GONE);
                lightText.setVisibility(View.GONE);
                logicBtn.setVisibility(View.GONE);
                logicText.setVisibility(View.GONE);
                if (appliancesInfo.getCurState() != null) {
                    float value = (float) appliancesInfo.getCurState();
                    sensorBtn.setText("传感器数值：" + value);
                }
                break;

        }
    }


    private void initcurState() {
//        lightState = 100;//初始化灯光亮度100
        curtainState = CurtainCtrlParser.curtainOff;

        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("hdl");


//        获取所有设备的状态。deviceStateBeanList为所有设备的状态列表
        List<DeviceStateBean> deviceStateBeanList = HDLCommand.getAllDevicesState();
//        第三方可根据自己的需求刷新设备页。以下为示例代码。
        for (DeviceStateBean deviceStateBean : deviceStateBeanList) {
            //注意！第三方应该将设备集合全部在这里遍历一遍以下操作，并更新状态值。这里仅作为Demo演示，只判断一个。

            //先判断此模块是否为当前设备的模块
            if (deviceStateBean.getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                    && deviceStateBean.getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()) {
//              再判断是什么类型的设备。
                switch (deviceStateBean.getDeviceType()) {
                    case HDLApConfig.TYPE_LIGHT_DIMMER:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_LIGHT_RELAY:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_LIGHT_MIX_DIMMER:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_LIGHT_MIX_RELAY:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_CURTAIN_GLYSTRO:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_CURTAIN_ROLLER:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_CURTAIN_MODULE:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setCurState(deviceStateBean.getCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_AC_HVAC:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setArrCurState(deviceStateBean.getArrCurState());
                        }
                        break;
                    case HDLApConfig.TYPE_AC_PANEL:
                        //判断是哪个回路的设备
                        if (deviceStateBean.getChannelNum() == appliancesInfo.getChannelNum()) {
                            appliancesInfo.setArrCurState(deviceStateBean.getArrCurState());
                        }
                        break;


                    default:
                        break;
                }
            }

        }
    }

    private void initClickOnEvent() {
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
                HDLCommand.curtainCtrl(appliancesInfo, 20);
            }
        });

        airBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //演示当前状态为关，设置为开。开，设置为关。
                if (airSwitchState == 0) {
                    HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSwich, AirCtrlParser.airOn);//空调开
                } else {
                    HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSwich, AirCtrlParser.airOff);//空调关
                }
            }
        });


        airBtnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (airModeState) {
                    case 0:
                        //若当前空调模式为制冷，则点击按钮设置为制热
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airMode, AirCtrlParser.airModeHeatTem);//空调模式制热
                        break;
                    case 1:
                        //若当前空调模式为制热，则点击按钮设置为通风
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airMode, AirCtrlParser.airModeVen);//空调模式通风
                        break;
                    case 2:
                        //若当前空调模式为通风，则点击按钮设置为自动
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airMode, AirCtrlParser.airModeAuto);//空调模式自动
                        break;
                    case 3:
                        //若当前空调模式为自动，则点击按钮设置为抽湿
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airMode, AirCtrlParser.airModeDehum);//空调模式抽湿
                        break;
                    case 4:
                        //若当前空调模式为抽湿，则点击按钮设置为制冷
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airMode, AirCtrlParser.airModeRefTem);//空调模式制冷
                        break;
                    default:
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airMode, AirCtrlParser.airModeRefTem);//空调模式制冷
                        break;

                }


            }
        });

        airBtnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (airSpeedState) {
                    case 0:
                        //若当前空调风速为自动，则点击按钮设置为高风
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSpeed, AirCtrlParser.airSpeedHigh);//风速高风
                        break;
                    case 1:
                        //若当前空调风速为高风，则点击按钮设置为中风
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSpeed, AirCtrlParser.airSpeedMid);//风速中风
                        break;
                    case 2:
                        //若当前空调风速为中风，则点击按钮设置为低风
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSpeed, AirCtrlParser.airSpeedLow);//风速低风
                        break;
                    case 3:
                        //若当前空调风速为低风，则点击按钮设置为自动
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSpeed, AirCtrlParser.airSpeedAuto);//风速自动
                        break;


                }
            }
        });

        airBtnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (airModeState) {
                    case 0:
                        //当前空调模式为制冷
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.refTem, Integer.parseInt(airTempEd.getText().toString()));//制冷温度
                        break;
                    case 1:
                        //当前空调模式为制热
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.heatTem, Integer.parseInt(airTempEd.getText().toString()));//制热温度
                        break;
                    case 2:
                        //当前空调模式为通风
                        ToastUtil("通风模式不能控制温度");
                        break;
                    case 3:
                        //当前空调模式为自动
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.autoTem, Integer.parseInt(airTempEd.getText().toString()));//自动温度 范围0-84
                        break;
                    case 4:
                        //当前空调模式为抽湿
                        HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.dehumTem, Integer.parseInt(airTempEd.getText().toString()));//抽湿温度 范围0-84
                        break;
                    default:
                        break;

                }

//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.upTem,1);//上升温度 范围0-5
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.downTem,1);//下降温度 范围0-5
            }
        });


        logicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.logicCtrl(appliancesInfo);
            }
        });

        sensorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.getDeviceState(appliancesInfo);
            }
        });
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
                            airSwitchState = 0;
                            airText.setText("空调关");
                            ToastUtil("空调关");
                            Log.i("djl", "空调关");
                            break;
                        case AirCtrlParser.airOn:
                            airSwitchState = 1;
                            airText.setText("空调开");
                            ToastUtil("空调开");
                            Log.i("djl", "空调开");
                            break;
                        default:
                            break;
                    }

                    break;

                case AirCtrlParser.airSpeed:
                    switch (curState[1] & 0xFF) {
                        case AirCtrlParser.airSpeedAuto:
                            airSpeedState = 0;
                            airText.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                            ToastUtil("空调风速，风速模式为：airSpeedAuto自动风速");
                            Log.i("djl", "空调风速，风速模式为：airSpeedAuto自动风速");
                            break;
                        case AirCtrlParser.airSpeedHigh:
                            airSpeedState = 1;
                            airText.setText("空调风速，风速模式为：airSpeedHigh风速高");
                            ToastUtil("空调风速，风速模式为：airSpeedHigh风速高");
                            Log.i("djl", "空调风速，风速模式为：airSpeedHigh风速高");
                            break;
                        case AirCtrlParser.airSpeedMid:
                            airSpeedState = 2;
                            airText.setText("空调风速，风速模式为：airSpeedMid风速中");
                            ToastUtil("空调风速，风速模式为：airSpeedMid风速中");
                            Log.i("djl", "空调风速，风速模式为：airSpeedMid风速中");
                            break;
                        case AirCtrlParser.airSpeedLow:
                            airSpeedState = 3;
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
                            airModeState = 0;
                            airText.setText("空调模式，模式为：制冷");
                            ToastUtil("空调模式，模式为：制冷");
                            Log.i("djl", "空调模式，模式为：制冷");
                            break;
                        case AirCtrlParser.airModeHeatTem:
                            airModeState = 1;
                            airText.setText("空调模式，模式为：制热");
                            ToastUtil("空调模式，模式为：制热");
                            Log.i("djl", "空调模式，模式为：制热");
                            break;
                        case AirCtrlParser.airModeVen:
                            airModeState = 2;
                            airText.setText("空调模式，模式为：通风");
                            ToastUtil("空调模式，模式为：通风");
                            Log.i("djl", "空调模式，模式为：通风");
                            break;
                        case AirCtrlParser.airModeAuto:
                            airModeState = 3;
                            airText.setText("空调模式，模式为：自动");
                            ToastUtil("空调模式，模式为：自动");
                            Log.i("djl", "空调模式，模式为：自动");
                            break;
                        case AirCtrlParser.airModeDehum:
                            airModeState = 4;
                            airText.setText("空调模式，模式为：抽湿");
                            ToastUtil("空调模式，模式为：抽湿");
                            Log.i("djl", "空调模式，模式为：抽湿");
                            break;
                        default:
                            airModeState = -1;
                            break;
                    }
                    break;
                case AirCtrlParser.refTem:
                    airTempState = curState[1] & 0xFF;
                    airText.setText("空调制冷，温度为：" + (curState[1] & 0xFF));
                    ToastUtil("空调制冷，温度为：" + (curState[1] & 0xFF));
                    Log.i("djl", "空调制冷，温度为：" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.heatTem:
                    airTempState = curState[1] & 0xFF;
                    airText.setText("空调制热，制热温度为" + (curState[1] & 0xFF));
                    ToastUtil("空调制热，制热温度为" + (curState[1] & 0xFF));
                    Log.i("djl", "空调制热，制热温度为" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.autoTem:
                    airTempState = -1;
                    airText.setText("空调自动，自动温度为" + (curState[1] & 0xFF));
                    ToastUtil("空调自动，自动温度为" + (curState[1] & 0xFF));
                    Log.i("djl", "空调自动，自动温度为" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.dehumTem:
                    airTempState = curState[1] & 0xFF;
                    airText.setText("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    ToastUtil("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    Log.i("djl", "空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.upTem:
                    airTempState = curState[1] & 0xFF;
                    airText.setText("空调调温，上升温度：" + (curState[1] & 0xFF));
                    ToastUtil("空调调温，上升温度：" + (curState[1] & 0xFF));
                    Log.i("djl", "空调调温，上升温度：" + (curState[1] & 0xFF));
                    break;
                case AirCtrlParser.downTem:
                    airTempState = curState[1] & 0xFF;
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
        if (event.getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                ) {
            //这个返回的信息是当前状态的
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
                    if (appliancesInfo.getChannelNum() == event.getAppliancesInfo().getChannelNum()) {
                        if (!event.isSuccess()) {
                            ToastUtil("获取窗帘状态失败，请重新再试");
                            return;
                        }
                        //窗帘模块：curState:0=停止,1=打开,2=关闭。
                        //开合帘电机，卷帘电机：curState:1-100开合度。
                        int curState = (int) event.getAppliancesInfo().getCurState();
                        if (event.getAppliancesInfo().getDeviceType() == HDLApConfig.TYPE_CURTAIN_MODULE) {//判断是否为窗帘模块,否则为开合帘或卷帘电机
                            switch (curState) {
                                case CurtainCtrlParser.curtainOff:
                                    curtainBtn.setText("窗帘关");
                                    curText1.setText("窗帘关");
                                    break;
                                case CurtainCtrlParser.curtainOn:
                                    curtainBtn.setText("窗帘开");
                                    curText1.setText("窗帘开");
                                    break;
                                case CurtainCtrlParser.curtainPause:
                                    curtainBtn.setText("窗帘暂停");
                                    curText1.setText("窗帘暂停");
                                    break;
                            }
                        } else {
                            curtainBtn5.setText("窗帘开到" + curState + "%");
                        }
                    }
                    break;
                case HDLApConfig.TYPE_AC_HVAC:
                case HDLApConfig.TYPE_AC_PANEL:
                    if (appliancesInfo.getChannelNum() == event.getAppliancesInfo().getChannelNum()) {
                        if (!event.isSuccess()) {
                            ToastUtil("获取空调状态失败，请重新再试");
                            return;
                        }

                        byte[] curState = event.getAppliancesInfo().getArrCurState();
                        switch (curState[0] & 0xFF) {
                            case AirCtrlParser.airSwich:
                                switch (curState[1] & 0xFF) {
                                    case AirCtrlParser.airOff:
                                        airSwitchState = 0;
                                        airText.setText("空调关");
                                        ToastUtil("空调关");
                                        Log.i("djl", "空调关");
                                        break;
                                    case AirCtrlParser.airOn:
                                        airSwitchState = 1;
                                        airText.setText("空调开");
                                        ToastUtil("空调开");
                                        Log.i("djl", "空调开");
                                        break;
                                    default:
                                        break;
                                }

                                break;

                            case AirCtrlParser.airSpeed:
                                switch (curState[1] & 0xFF) {
                                    case AirCtrlParser.airSpeedAuto:
                                        airSpeedState = 0;
                                        airText.setText("空调风速，风速模式为：airSpeedAuto自动风速");
                                        ToastUtil("空调风速，风速模式为：airSpeedAuto自动风速");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedAuto自动风速");
                                        break;
                                    case AirCtrlParser.airSpeedHigh:
                                        airSpeedState = 1;
                                        airText.setText("空调风速，风速模式为：airSpeedHigh风速高");
                                        ToastUtil("空调风速，风速模式为：airSpeedHigh风速高");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedHigh风速高");
                                        break;
                                    case AirCtrlParser.airSpeedMid:
                                        airSpeedState = 2;
                                        airText.setText("空调风速，风速模式为：airSpeedMid风速中");
                                        ToastUtil("空调风速，风速模式为：airSpeedMid风速中");
                                        Log.i("djl", "空调风速，风速模式为：airSpeedMid风速中");
                                        break;
                                    case AirCtrlParser.airSpeedLow:
                                        airSpeedState = 3;
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
                                        airModeState = 0;
                                        airText.setText("空调模式，模式为：制冷");
                                        ToastUtil("空调模式，模式为：制冷");
                                        Log.i("djl", "空调模式，模式为：制冷");
                                        break;
                                    case AirCtrlParser.airModeHeatTem:
                                        airModeState = 1;
                                        airText.setText("空调模式，模式为：制热");
                                        ToastUtil("空调模式，模式为：制热");
                                        Log.i("djl", "空调模式，模式为：制热");
                                        break;
                                    case AirCtrlParser.airModeVen:
                                        airModeState = 2;
                                        airText.setText("空调模式，模式为：通风");
                                        ToastUtil("空调模式，模式为：通风");
                                        Log.i("djl", "空调模式，模式为：通风");
                                        break;
                                    case AirCtrlParser.airModeAuto:
                                        airModeState = 3;
                                        airText.setText("空调模式，模式为：自动");
                                        ToastUtil("空调模式，模式为：自动");
                                        Log.i("djl", "空调模式，模式为：自动");
                                        break;
                                    case AirCtrlParser.airModeDehum:
                                        airModeState = 4;
                                        airText.setText("空调模式，模式为：抽湿");
                                        ToastUtil("空调模式，模式为：抽湿");
                                        Log.i("djl", "空调模式，模式为：抽湿");
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case AirCtrlParser.refTem:
                                airTempState = curState[1] & 0xFF;
                                airText.setText("空调制冷，温度为：" + (curState[1] & 0xFF));
                                ToastUtil("空调制冷，温度为：" + (curState[1] & 0xFF));
                                Log.i("djl", "空调制冷，温度为：" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.heatTem:
                                airTempState = curState[1] & 0xFF;
                                airText.setText("空调制热，制热温度为" + (curState[1] & 0xFF));
                                ToastUtil("空调制热，制热温度为" + (curState[1] & 0xFF));
                                Log.i("djl", "空调制热，制热温度为" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.autoTem:
                                airTempState = curState[1] & 0xFF;
                                airText.setText("空调自动，自动温度为" + (curState[1] & 0xFF));
                                ToastUtil("空调自动，自动温度为" + (curState[1] & 0xFF));
                                Log.i("djl", "空调自动，自动温度为" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.dehumTem:
                                airTempState = curState[1] & 0xFF;
                                airText.setText("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                                ToastUtil("空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                                Log.i("djl", "空调抽湿，抽湿温度为" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.upTem:
                                airTempState = curState[1] & 0xFF;
                                airText.setText("空调调温，上升温度：" + (curState[1] & 0xFF));
                                ToastUtil("空调调温，上升温度：" + (curState[1] & 0xFF));
                                Log.i("djl", "空调调温，上升温度：" + (curState[1] & 0xFF));
                                break;
                            case AirCtrlParser.downTem:
                                airTempState = curState[1] & 0xFF;
                                airText.setText("空调调温，下降温度：" + (curState[1] & 0xFF));
                                ToastUtil("空调调温，下降温度：" + (curState[1] & 0xFF));
                                Log.i("djl", "空调调温，下降温度：" + (curState[1] & 0xFF));
                                break;
                        }
                    }
                    break;
                case HDLApConfig.TYPE_SENSOR_DRY_CONTACT:
                    //传感器 干接点 。只有开关状态
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---干接点状态失败，请重新再试");
                        return;
                    }
                    float dryContactValue = (float) event.getAppliancesInfo().getCurState();
                    String dryContactUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---干接点状态：" + dryContactValue + dryContactUnit);
                    Log.i("djl", "传感器---干接点状态：" + dryContactValue + dryContactUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_MOVEMENT_DETECTOR:
                    //传感器 移动探测 。灵敏度
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---移动探测灵敏度状态失败，请重新再试");
                        return;
                    }
                    float mdValue = (float) event.getAppliancesInfo().getCurState();
                    String mdValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---移动探测灵敏度：" + mdValue + mdValueUnit);
                    Log.i("djl", "传感器---移动探测灵敏度：" + mdValue + mdValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_TEMP:
                    //传感器 温度
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---温度状态失败，请重新再试");
                        return;
                    }
                    float tempValue = (float) event.getAppliancesInfo().getCurState();
                    String tempValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---温度：" + tempValue + tempValueUnit);
                    Log.i("djl", "传感器---温度：" + tempValue + tempValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_HUMIDITY:
                    //传感器 湿度
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---湿度状态失败，请重新再试");
                        return;
                    }
                    float humidityValue = (float) event.getAppliancesInfo().getCurState();
                    String humidityValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---湿度：" + humidityValue + humidityValueUnit);
                    Log.i("djl", "传感器---湿度：" + humidityValue + humidityValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_ILLUMINACE:
                    //传感器 照度
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---照度状态失败，请重新再试");
                        return;
                    }
                    float illuminaceValue = (float) event.getAppliancesInfo().getCurState();
                    String illuminaceValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---照度：" + illuminaceValue + illuminaceValueUnit);
                    Log.i("djl", "传感器---照度：" + illuminaceValue + illuminaceValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_VOC:
                    //传感器 当前空气质量等级
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---当前空气质量等级状态失败，请重新再试");
                        return;
                    }
                    float vocValue = (float) event.getAppliancesInfo().getCurState();
                    String vocValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---当前空气质量等级：" + vocValue + vocValueUnit);
                    Log.i("djl", "传感器---当前空气质量等级：" + vocValue + vocValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_PM_2_POINT_5:
                    //传感器 pm2.5
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---pm2.5状态失败，请重新再试");
                        return;
                    }
                    float pm2_5Value = (float) event.getAppliancesInfo().getCurState();
                    String pm2_5ValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---pm2.5：" + pm2_5Value + pm2_5ValueUnit);
                    Log.i("djl", "传感器---pm2.5：" + pm2_5Value + pm2_5ValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_C02:
                    //传感器 二氧化碳
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---二氧化碳状态失败，请重新再试");
                        return;
                    }
                    float co2Value = (float) event.getAppliancesInfo().getCurState();
                    String co2ValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---二氧化碳：" + co2Value + co2ValueUnit);
                    Log.i("djl", "传感器---二氧化碳：" + co2Value + co2ValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_LPG:
                    //传感器 液化石油气
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---液化石油气状态失败，请重新再试");
                        return;
                    }
                    float lpgValue = (float) event.getAppliancesInfo().getCurState();
                    String lpgValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---液化石油气：" + lpgValue + lpgValueUnit);
                    Log.i("djl", "传感器---液化石油气：" + lpgValue + lpgValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_CO_H2:
                    //传感器 人工煤气
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---人工煤气状态失败，请重新再试");
                        return;
                    }
                    float coh2Value = (float) event.getAppliancesInfo().getCurState();
                    String coh2ValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---人工煤气：" + coh2Value + coh2ValueUnit);
                    Log.i("djl", "传感器---人工煤气：" + coh2Value + coh2ValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_CH4:
                    //传感器 天然气
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---天然气状态失败，请重新再试");
                        return;
                    }
                    float ch4Value = (float) event.getAppliancesInfo().getCurState();
                    String ch4ValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---天然气：" + ch4Value + ch4ValueUnit);
                    Log.i("djl", "传感器---天然气：" + ch4Value + ch4ValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_SMOG:
                    //传感器 烟雾
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---烟雾状态失败，请重新再试");
                        return;
                    }
                    float smogValue = (float) event.getAppliancesInfo().getCurState();
                    String smogValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---烟雾：" + smogValue + smogValueUnit);
                    Log.i("djl", "传感器---烟雾：" + smogValue + smogValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_WIND_SPEED:
                    //传感器 风速
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---风速状态失败，请重新再试");
                        return;
                    }
                    float windSpeedValue = (float) event.getAppliancesInfo().getCurState();
                    String windSpeedValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---风速：" + windSpeedValue + windSpeedValueUnit);
                    Log.i("djl", "传感器---风速：" + windSpeedValue + windSpeedValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_WIND_PRESSURE:
                    //传感器 风压
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---风压状态失败，请重新再试");
                        return;
                    }
                    float windPressureValue = (float) event.getAppliancesInfo().getCurState();
                    String windPressureValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---风压：" + windPressureValue + windPressureValueUnit);
                    Log.i("djl", "传感器---风压：" + windPressureValue + windPressureValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_LIQUID_FLOW:
                    //传感器 液体流量
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---液体流量状态失败，请重新再试");
                        return;
                    }
                    float liquidFlowValue = (float) event.getAppliancesInfo().getCurState();
                    String liquidFlowValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---液体流量：" + liquidFlowValue + liquidFlowValueUnit);
                    Log.i("djl", "传感器---液体流量：" + liquidFlowValue + liquidFlowValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_LIQUID_PRESSURE:
                    //传感器 液体压力
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---液体压力状态失败，请重新再试");
                        return;
                    }
                    float liquidPressureValue = (float) event.getAppliancesInfo().getCurState();
                    String liquidPressureValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---液体压力：" + liquidPressureValue + liquidPressureValueUnit);
                    Log.i("djl", "传感器---液体压力：" + liquidPressureValue + liquidPressureValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_LIQUID_DEPTH:
                    //传感器 液体深度
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---液体深度状态失败，请重新再试");
                        return;
                    }
                    float liquidDepthValue = (float) event.getAppliancesInfo().getCurState();
                    String liquidDepthValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---液体深度：" + liquidDepthValue + liquidDepthValueUnit);
                    Log.i("djl", "传感器---液体深度：" + liquidDepthValue + liquidDepthValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_RAIN_FALL:
                    //传感器 雨量
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---雨量状态失败，请重新再试");
                        return;
                    }
                    float rainFallValue = (float) event.getAppliancesInfo().getCurState();
                    String rainFallValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---雨量：" + rainFallValue + rainFallValueUnit);
                    Log.i("djl", "传感器---雨量：" + rainFallValue + rainFallValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_WEIGHT:
                    //传感器 重量
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---重量状态失败，请重新再试");
                        return;
                    }
                    float weightValue = (float) event.getAppliancesInfo().getCurState();
                    String weightValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---重量：" + weightValue + weightValueUnit);
                    Log.i("djl", "传感器---重量：" + weightValue + weightValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_HEIGHT_LENGTH:
                    //传感器 重量
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---重量状态失败，请重新再试");
                        return;
                    }
                    float height_lengthValue = (float) event.getAppliancesInfo().getCurState();
                    String height_lengthValueUnit = ((SensorStateBackInfo) event).getUnite();
                    ToastUtil("传感器---重量：" + height_lengthValue + height_lengthValueUnit);
                    Log.i("djl", "传感器---重量：" + height_lengthValue + height_lengthValueUnit);
                    break;
                case HDLApConfig.TYPE_SENSOR_OBJECT_SPEED:
                    //传感器 物体速度
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---物体速度状态失败，请重新再试");
                        return;
                    }
                    float objectSpeedValue = (float) event.getAppliancesInfo().getCurState();
                    String objectSpeedUnite = ((SensorStateBackInfo) event).getUnite();//数值单位
                    ToastUtil("传感器---物体速度：" + objectSpeedValue + objectSpeedUnite);
                    Log.i("djl", "传感器---物体速度：" + objectSpeedValue + objectSpeedUnite);
                    break;
                case HDLApConfig.TYPE_SENSOR_SHAKE:
                    //传感器 震动
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---震动状态失败，请重新再试");
                        return;
                    }
                    float shakeValue = (float) event.getAppliancesInfo().getCurState();
                    String shakeValueUnite = ((SensorStateBackInfo) event).getUnite();//数值单位
                    ToastUtil("传感器---震动：" + shakeValue + shakeValueUnite);
                    Log.i("djl", "传感器---震动：" + shakeValue + shakeValueUnite);
                    break;
                case HDLApConfig.TYPE_SENSOR_VOLTAGE:
                    //传感器 电压
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---电压状态失败，请重新再试");
                        return;
                    }
                    float voltageValue = (float) event.getAppliancesInfo().getCurState();
                    String voltageValueUnite = ((SensorStateBackInfo) event).getUnite();//数值单位
                    ToastUtil("传感器---电压：" + voltageValue + voltageValueUnite);
                    Log.i("djl", "传感器---电压：" + voltageValue + voltageValueUnite);
                    break;
                case HDLApConfig.TYPE_SENSOR_ELECTRICITY:
                    //传感器 电流
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---电流状态失败，请重新再试");
                        return;
                    }
                    float electricityValue = (float) event.getAppliancesInfo().getCurState();
                    String electricityValueUnite = ((SensorStateBackInfo) event).getUnite();//数值单位
                    ToastUtil("传感器---电流：" + electricityValue + electricityValueUnite);
                    Log.i("djl", "传感器---电流：" + electricityValue + electricityValueUnite);
                    break;
                case HDLApConfig.TYPE_SENSOR_POWER:
                    //传感器 功率
                    if (appliancesInfo.getChannelNum() != event.getAppliancesInfo().getChannelNum() || !event.isSuccess()) {
                        ToastUtil("获取传感器---功率状态失败，请重新再试");
                        return;
                    }
                    float powerValue = (float) event.getAppliancesInfo().getCurState();
                    String powerValueUnite = ((SensorStateBackInfo) event).getUnite();//数值单位
                    ToastUtil("传感器---功率：" + powerValue + powerValueUnite);
                    Log.i("djl", "传感器---功率：" + powerValue + powerValueUnite);
                    break;


            }
        }
    }


    public void ToastUtil(String text) {
        Toast.makeText(CtrlActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
