# HDL Lib SDK通讯协议文档

   此SDK仅针对安卓平台进行集成，旨在集成HDL SDK后，可调用相关API，实现HDL设备的搜索、控制、获取当前状态等。在文档最后会提供demo示例，详情请查看demo。以下详细列出HDL SDK集成的相关信息：
   
## 1：平台条件

1.1 目前仅支持Android开发平台，Android SDK 版本4.2以上。

1.2 目前支持Android Studio IDE集成，通过依赖 `compile ‘com.hdl.lib:hdllib:1.2.13’ `即可成功将HDL SDK集成到项目中。（由于Bintay方面还在审核1.2.13版本，存在依赖不成功的可能，若不成功请依赖1.2.5，但建议依赖最新的版本）。

1.3 Android Studio平台也支持提供arr包依赖方式，此种方式可随时拿到最新的SDK版本。

1.4 支持Eclipse 安卓开发平台，此种方式可提供jar包依赖，由于SDK有依赖其他第三方库，存在此平台支持不理想的情况，建议转到1.2或1.3方式。

1.5 调试SDK建议使用真机调试，模拟器可能会导致一些不知名的问题。

## 2：SDK初始化

2.1 在build.gradle文件上依赖相应的库

2.1.1 `compile(name: 'hdl_lib-v1.2.13', ext: 'aar')`此种方式依赖为aar文件依赖，此aar包为HDL Lib的通讯包。可向相关开发人员索取最新aar包，`compile 'com.hdl.lib:hdllib:1.2.13'`此种方式与aar包依赖方式同样效果（详情请看demo）

2.1.2   `compile 'org.greenrobot:eventbus:3.0.0' ` 这个依赖包为接收HDL Lib的EventBusEvent事件，必须依赖才能接收。（详情请看demo）

2.1.3  `compile 'com.squareup.okhttp3:okhttp:3.4.1'`这个为接收On设备的okhttp包，非必须依赖，若要集成On设备获取api则必须依赖。

2.2 在需要调用的activity中做初始化操作：`DeviceManager.init(this);`（此操作已经初始化EventBus，具体请查看demo）

2.3 SDK初始化的端口为6000，若有其他程序占用6000端口，则SDK无法初始化，报错。
![Alt text](https://github.com/TommyDaiJ/HdlSdkDemo/blob/master/app/src/main/res/drawable/img1.png)

## 3：搜索设备

3.1 HDL SDK提供搜索设备的api，等待5秒后返回设备信息。

3.2 调用`CommandData.devicesSearch(MainActivity.this);`

3.3 必须在此activity中实现EventBus的方法，（具体请查看demo）搜索返回：

``` 
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
    
```


## 4：获取设备信息
在搜索中获取到信息为设备信息，在demo中的ApplianceActivity显示设备信息。设备信息列表显示的是此设备所有回路设备。

4.1 ApplianceActivity中必须初始化EventBus（具体请查看demo），调用CommandData.getAppliancesRemarks(AppliancesActivity.this, appliancesInfos);获取到的每个回路的备注。

4.2 接收备注回调，调用EventBus的返回

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
    
## 5 获取相关设备状态

5.1调用CommandData.getDeviceState(CtrlActivity.this,appliancesInfo);两个参数为固定参数。即可获取相关设备对应回路的状态，必须要调用EventBus接收返回信息，具体请查看demo

## 6 控制设备

### 6.1灯光控制

6.1.1 调用CommandData.lightCtrl(CtrlActivity.this,appliancesInfo,state);第三个参数为灯光亮度，0代表关，范围在0-100.超过100不做处理。

6.1.2需要接收EventBus的控制返回结果，具体请查看demo。

### 6.2 窗帘控制

窗帘种类有：窗帘模块，卷帘电机，开合帘电机。

6.2.1 调`CommandData.curtainCtrl(Context context, AppliancesInfo info, int state)`前两个参数为固定参数，跟6.1.1雷同。第三个参数为：`CurtainCtrlParser.curtainPause`（窗帘停）或`CurtainCtrlParser.curtainOn`（窗帘开）或`CurtainCtrlParser.curtainOff`（窗帘关） 中的一个。
窗帘模块只能调用这3个参数，卷帘电机和开合帘电机第二个参数可以填0-100数字，代表百分比。



6.2.3 需要接收EventBus的控制返回结果，具体查看demo。仅将`lightCtrlBackInfo`改为`CurtainCtrlBackInfo`

### 6.3 空调控制

6.3.1调用相关控制空调api即可。


```
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
//                CommandData.airCtrl(CtrlActivity.this,appliancesInfo,AirCtrlParser.downTem,1);//下降温度 范围0-5

```


### 6.4 逻辑模块控制

6.4.1调用CommandData.logicCtrl(CtrlActivity.this,appliancesInfo);具体查看demo

# 7 接收设备状态改变推送

sdk可接收设备状态改变的推送，目前支持灯光，窗帘，空调面板的状态改变推送。在需要接收的界面重写EventBus回调。

### 7.1 接收灯光的推送

```
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
        Toast.makeText(this,parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 亮度为："+brightness,Toast.LENGTH_SHORT).show();

        /**
         * 如果备注不能满足需求，则可通过子网id和设备id查找。子网id，设备id共同确定唯一设备。
         */



    }
```

### 7.2 接收窗帘的推送

```
   @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurtainFeedBackInfoEventMain(CurtainFeedBackEvent event){
        int curState = event.getCurtainCtrlBackInfo().getState();
        //窗帘模块：curState:0=停止,1=打开,2=关闭。
        //开合帘电机，卷帘电机：curState:1-100开合度。也会返回0，1，2的状态
        //建议开合帘电机，卷帘电机按停止后再读取当前状态来获取当前状态值

        String remarks = event.getCurtainCtrlBackInfo().getRemarks();
        String parentRemarks = event.getCurtainCtrlBackInfo().getParentRemarks();
        int num = event.getCurtainCtrlBackInfo().getNum();
        Toast.makeText(this,parentRemarks+" 的 "+remarks+" 回路号："+num+" 返回"+" 状态为："+curState,Toast.LENGTH_SHORT).show();

    }

```

### 7.3 接收空调面板的推送

```

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

```




# 8 HDL On软件设备数据获取

8.1 调用`OnManager.getOnDevicesData("192.168.2.113");`参数填写On设备上分享的ip地址。使用如下方法来接收数据。目前只能接收：调光回路，开关回路，开合帘电机，卷帘电机，窗帘模块，通用空调面板 的数据。接收到这些设备后，均可以用以上控制，获取状态等API加以操作。

	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onSowInfoEventMain(OnDeviceDataEvent event){
        OndevicesDatas = event.getDevicesDataList();
        for(int i=0;i<OndevicesDatas.size();i++){
            List<AppliancesInfo> appliancesInfoList = OndevicesDatas.get(i).getAppliancesInfoList();
            for(int j=0;j<appliancesInfoList.size();j++){
                Log.i("djl","设备名称："+appliancesInfoList.get(j).getDeviceName()
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



# 9 Demo下载链接 ：
[HDL Lib SDK Demo](https://github.com/TommyDaiJ/HdlSdkDemo)
    
    
