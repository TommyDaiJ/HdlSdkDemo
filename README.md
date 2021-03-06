# HDL Android Lib SDK通讯协议文档
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-glide--transformations-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1363)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

   此SDK仅针对安卓平台进行集成，旨在集成HDL SDK后，可调用相关API，实现HDL设备的搜索、控制、获取当前状态等。在文档最后会提供demo示例，详情请查看demo。以下详细列出HDL SDK集成的相关信息：
   
   


#  How do I use it?

## Step 1

#### Gradle


1.1 目前仅支持Android开发平台，Android SDK 版本4.2以上。

1.2 支持Android Studio IDE集成。

```
 
dependencies {
    compile(name:'hdl_lib_v1.5.8.20190227_beta', ext:'aar')
}

```



1.3 调试SDK建议使用真机调试，模拟器的ip地址问题会发生错误。

## Step 2：SDK初始化

2.1.1 这个依赖包为接收HDL Lib的EventBusEvent事件，必须依赖才能接收。（详情请看demo）

```

dependencies {
    compile 'org.greenrobot:eventbus:3.0.0'
}

```

2.1.2 这个为接收On设备的okhttp包，非必须依赖，若要集成On设备获取api则必须依赖。

```

dependencies {
    compile 'com.squareup.okhttp3:okhttp:3.4.1'
}

```

2.2 可在LaunchActivity或开启Service初始化：

```
HDLDeviceManager.init(Context context);
```

（因不同厂家需要此操作从1.2.14版本后不再初始化EventBus，可自行初始化EventBus具体请查看demo）。因SDK初始化仅仅开启一个线程做接收、发送操作，程序应确保该线程存活。建议使用Service初始化SDK`Context.startService()`，Service不能新开进程初始化SDK，因为SDK使用EventBus通讯，EventBus不支持跨进程通讯。若要使用双进程保活机制，需要注意将SDK初始化放在同一进程这个问题。

2.3 SDK初始化的端口为6000，若有其他程序占用6000端口，则SDK无法初始化，报错。


![Alt text](https://github.com/TommyDaiJ/HdlSdkDemo/blob/master/app/src/main/res/drawable/img1.png)


## Step 3 调用相关API

### 3：搜索设备

3.1 HDL SDK提供搜索设备的api，稍等数秒返回设备信息。

3.2 调用`HDLCommand.getHomeDevices(Context context);`获取HDL家居设备数据

3.3 调用`HDLCommand.getRcuDevices(Context context,String rcuIp);`获取HDL酒店设备数据

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


```

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

```



### 4：获取设备信息
在搜索中获取到信息为设备信息，在demo中的ApplianceActivity显示设备信息。设备信息列表显示的是此设备所有回路设备。 如果需要确定哪个设备哪个回路，则可通过子网id和设备id，大类，小类，回路号。

4.1 ApplianceActivity中必须初始化EventBus（具体请查看demo）

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

4.3 获取设备类型
通过 `appliancesInfo.getDeviceType()` 可获取到设备类型。具体参数如下

```
//1：调光回路（灯） 2：开关回路（灯） 3：混合调光类 （灯） 4：混合开关类（灯）
// 5：开合帘电机（窗帘）6：卷帘电机（窗帘） 7：窗帘模块 （窗帘）8：HVAC 模块(空调)
// 9：通用空调面板(空调) 10：背景音乐模块（音乐） 11：第三方背景音乐模块（音乐）
/ 12：逻辑模块（场景） 13：全局逻辑模块（场景）

// 可以直接总结为：
//1、2、3、4 为灯
//5、6、7 为窗帘
//8、9 为空调
//10、11 为音乐
//12、13 为场景

appliancesInfo.getDeviceType()

```

### 5 获取单一回路设备状态

5.1调用`HDLCommand.getDeviceState(appliancesInfo);`参数为固定参数。即可获取相关设备对应回路的状态，必须要调用EventBus接收返回信息，具体请查看demo。一般情况下都不需要用到。在搜索回来的信息中，已可通过获取当前信息的方法获取每一回路的状态，具体请见Demo

### 6 控制设备

#### 6.1灯光控制

6.1.1 调用`HDLCommand.lightCtrl(appliancesInfo,state);`第三个参数为灯光亮度，0代表关，范围在0-100.超过100不做处理。

6.1.2需要接收EventBus的控制返回结果，具体请查看demo。

#### 6.2 窗帘控制

窗帘种类有：窗帘模块，卷帘电机，开合帘电机。

6.2.1 调`HDLCommand.curtainCtrl( AppliancesInfo info, int state)`前两个参数为固定参数，跟6.1.1雷同。第三个参数为：`CurtainCtrlParser.HDLcurtainPause`（窗帘停）或`CurtainCtrlParser.HDLcurtainOn`（窗帘开）或`CurtainCtrlParser.HDLcurtainOff`（窗帘关） 中的一个。
窗帘模块只能调用这3个参数，卷帘电机和开合帘电机第二个参数可以填0-100数字，代表百分比。



6.2.3 需要接收EventBus的控制返回结果，具体查看demo。仅将`lightCtrlBackInfo`改为`CurtainCtrlBackInfo`

#### 6.3 空调控制

6.3.1调用相关控制空调api即可。


```
                  HDLCommand.airCtrl(appliancesInfo, AirCtrlParser.airSwich,AirCtrlParser.airOn);//空调面板开
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSwich,AirCtrlParser.airOff);//空调面板关
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.refTem,20);//制冷温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedAuto);//风速自动
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedHigh);//风速高风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedMid);//风速中风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airSpeed,AirCtrlParser.airSpeedLow);//风速低风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeRefTem);//空调模式制冷
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeHeatTem);//空调模式制热
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeVen);//空调模式通风
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeAuto);//空调模式自动
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.airMode,AirCtrlParser.airModeDehum);//空调模式抽湿
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.heatTem,28);//制热温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.autoTem,25);//自动温度 范围0-84
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.upTem,1);//上升温度 范围0-5
//                HDLCommand.airCtrl(appliancesInfo,AirCtrlParser.downTem,1);//下降温度 范围0-5

```


#### 6.4 逻辑模块控制

6.4.1调用HDLCommand.logicCtrl(appliancesInfo);具体查看demo

## 7 接收设备状态改变推送

sdk可接收设备状态改变的推送，目前支持灯光，窗帘，空调面板的状态改变推送。在需要接收的界面重写EventBus回调。

#### 7.1 接收灯光的推送

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





    }
```

#### 7.2 接收窗帘的推送

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

#### 7.3 接收空调面板的推送

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

## 8 音乐集成
相关的调用api:(具体查看demo)

```
        HDLCommand.audioCtrl(appliancesInfo,HDLAudio.GET_AUDIO_CURRRENT_INFO);//获取当前音乐信息。返回当前歌曲、列表等所有信息。获取音乐信息当前音乐会停止播放
        HDLCommand.audioCtrl(appliancesInfo,HDLAudio.GET_AUDIO_MODE);//获取当前音乐播放模式。仅返回单曲播放等播放模式。
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_CHOOSE_PLAY_SONG,curListNum,position);
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_PLAYPAUSE);
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_PLAYSTOP);
            }
        });

        preSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_PRE_SONG);
            }
        });

        nextSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_NEXT_SONG);
            }
        });

        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_MODE_UP);//播放模式+
//                HDLCommand.HDLaudioCtrl(AudioActivity.this,appliancesInfo,HDLAudio.SET_AUDIO_MODE_DOWN);//播放模式-
            }
        });

        volMinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_VOL,0);//音量最小：0。小于0，SDK不处理
            }
        });

        volMidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_VOL,40);
            }
        });

        volMaxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_VOL,79);//音量最大：79。超过79，SDK不处理
            }
        });

        nextListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_NEXT_LIST);//获取下一列表，当前音乐会停止播放
            }
        });

        preListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.audioCtrl(appliancesInfo,HDLAudio.SET_PRE_LIST);//获取上一列表，当前音乐会停止播放
            }
        });
```


```
        if(event.getAppliancesInfo().getDeviceSubnetID() == appliancesInfo.getDeviceSubnetID()
                && event.getAppliancesInfo().getDeviceDeviceID() == appliancesInfo.getDeviceDeviceID()
                ){
            switch (event.getType()){
                case HDLAudio.CALLBACK_SONG_NAME_LIST:
                    listString.clear();
                    for(int i=0;i<event.getSongNameList().size();i++){
                        listString.add(event.getSongNameList().get(i));
                    }

                    adapter.notifyDataSetChanged();
                    break;
                case HDLAudio.CALLBACK_CURRENT_VOLUME:
                    Log.i("djl","当前音量值："+event.getAudioInfoInt());
                    break;
                case HDLAudio.CALLBACK_AUDIO_LIST_NUM:
                    int[] listNum = event.getAudioListInfo();
                    curListNum = listNum[0];
                    Log.i("djl","当前列表号："+listNum[0]+" 当前共有列表数："+listNum[1]);
                    break;
                case HDLAudio.CALLBACK_CURRENT_LIST_NAME:
                    Log.i("djl","当前列表名："+event.getAudioInfoStr());
                    break;
                case HDLAudio.CALLBACK_CURRENT_SONG_NUM:
                    int[] songNum = event.getAudioListInfo();
                    Log.i("djl","当前歌曲号："+songNum[0]+" 当前共有歌曲数："+songNum[1]);
                    break;
                case HDLAudio.CALLBACK_CURRENT_SONG_NAME:
                    Log.i("djl","当前歌曲名："+event.getAudioInfoStr());
                    curSongNameTv.setText("当前歌曲名："+event.getAudioInfoStr());
                    break;
                case HDLAudio.CALLBACK_CURRENT_SONG_INFO:
                    int[] songInfo = event.getAudioListInfo();
                    //songInfo[0],songInfo[1]获得的值为秒，如songInfo[0]=250，即歌曲总时长为250秒。songInfo[2]获得的值为：1、2、3。1：停止，2：播放，3：暂停。
                    String curStatus ;
                    switch (songInfo[2]){
                        case 1:
                            curStatus = "停止";
                            break;
                        case 2:
                            curStatus = "播放";
                            break;
                        case 3:
                            curStatus = "暂停";
                            break;
                        default:
                            curStatus = "未知";
                            break;
                    }
                    Log.i("djl","当前歌曲总时长："+songInfo[0]+"秒 ，当前歌曲已播放时长："+songInfo[1]+"秒， 当前歌曲状态："+curStatus);
                    curSongInfoTv.setText("当前歌曲总时长："+songInfo[0]+"秒 ，当前歌曲已播放时长："+songInfo[1]+"秒， 当前歌曲状态："+curStatus);
                    break;
                case HDLAudio.CALLBACK_CURRENT_MODE:
                    String curMode ;
                    switch (event.getAudioInfoInt()){
                        case 1:
                            curMode = "单曲播放";
                            break;
                        case 2:
                            curMode = "单曲循环";
                            break;
                        case 3:
                            curMode = "连续播放";
                            break;
                        case 4:
                            curMode = "连播循环";
                            break;
                        default:
                            curMode = "未知";
                            break;

                    }
                    modeBtn.setText(curMode);
                    break;
                default:
                    break;
            }
        }
```




## 9 接收HDL设备警报消息

```
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWarningEventMain(WarningInfoEvent event){
        String warningType = event.getWaringType();
        Toast.makeText(MainActivity.this,warningType,Toast.LENGTH_SHORT).show();
    }

```

静态注册接收消息，可根据自身情况选择

```
        <receiver
            android:name=".HDLBroacastRv">
            <intent-filter>
                <action android:name="com.hdl.action.WARNING"/>
            </intent-filter>
        </receiver>
```

```
public class HDLBroacastRv extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String warningType = intent.getStringExtra("HdlWarning");
        Toast.makeText(context,warningType,Toast.LENGTH_SHORT).show();
    }
}
```



## 10 接收HDL 音乐数据转发与发送相关命令
10.1：使用此方法接收音乐数据
```
@Subscribe(threadMode = ThreadMode.MAIN)
    public void onBgmEventMain(ThirdPartyBgmInfoEvent event) {
        byte[] bgmBytes = event.getBytes();
    }
```


10.2：使用此方法发送自定义命令
```
HDLCommand.cusSendCommand(int command, int subnetID, int deviceID, byte[] addBytes, int port, String ipAddress)
```
参数解析：  command：操作码、subnetID：子网号、deviceID：设备号、addBytes：附加数据、port：端口号、ipAddress：ip地址





## 11 HDL On软件设备数据获取

```
OnManager.getOnDevicesData("Your Ip Address");
```
参数填写On设备上分享的ip地址。使用如下方法来接收数据。目前只能接收：调光回路，开关回路，开合帘电机，卷帘电机，窗帘模块，通用空调面板 的数据。接收到这些设备后，均可以用以上控制，获取状态等API加以操作。

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

# 版本更新
#### v1.5.8.20181218_beta
beta版本非稳定版本
1：修复搜索过程中有可能导致崩溃的bug


#### v1.5.8.20181101_beta
beta版本非稳定版本
1：增加相关日志，方便排查对接问题。

#### v1.5.8.20181024_beta
beta版本非稳定版本
1：修复窗帘模块只能控制第一回路的bug。

#### v1.5.8.20181011_2_beta
beta版本非稳定版本
1：修复HVAC空调控制,命令错误导致广播数据错误bug。

#### v1.5.8.20181011_beta
beta版本非稳定版本
1：修复HVAC空调控制bug

#### v1.5.8.20180929_beta
beta版本非稳定版本
1：修复获取备注可能产生的空指针问题。

#### v1.5.8.20180919_beta
beta版本非稳定版本
1：将音乐转发数据限定为只转发音乐协议数据

#### v1.5.8.20180918_beta
beta版本非稳定版本
1：修复在未控制设备的状态下，无法收到相关命令的bug

#### v1.5.8.20180913.3_beta
beta版本非稳定版本
1：修复空调控制未搜到备注，控制崩溃bug

#### v1.5.8.20180911_beta
beta版本非稳定版本
1：修复SDK在搜索过程中，音乐、空调命令导致相关接口崩溃bug

#### v1.5.8.20180907_beta
beta版本非稳定版本
1：修复SDK搜索机制、获取备注机制。提供第三方背景音乐获取接口

#### v1.5.8.20180906_beta
beta版本非稳定版本
1：修复SDK逻辑模块获取备注问题


#### v1.5.8.20180905_beta
beta版本非稳定版本
1：修复酒店空调无法控制
2：修复SDK切换家居、酒店导致备注获取失败的bug
3：修复酒店窗帘控制不稳定。

#### v1.5.4
1:更新 v1.5.4 修复备注不回复,SDK会重复获取的bug。
2:调整控制失败逻辑

#### v1.5.3.1
1:增加搜索、控制设备的超时判断，具体请查看demo,CtrlActivity使用。

#### v1.5.2.17_beta
1:修复窗帘返回百分比不准确bug

#### v1.5.2.2
1:修复多次控制灯光出现的崩溃。提供方法判断设备类型。详情查看demo CtrlActivity。

#### v1.5.1
1:SDK 集成了HDL RCU协议，第三无需改动目前的接口即可控制、接收RCU相关命令。

#### v1.4.6
1:增加背景音乐获取列表api。

#### v1.4.5
1:增加判断背景音乐回复数据是否为当前所需数据，修复因此导致当前列表歌曲数获取有误Bug

#### v1.4.4
1:修复由于音乐备注没有返回，导致SDK不回复数据

#### v1.4.3
1:舍弃HDLCommand.getAppliancesRemarks(AppliancesInfo appliancesInfos)获取备注api。设备备注SDK自行获取，第三方只需读取即可。v1.4.1版本Demo已不再使用此api,此版本将去除此api。

2：优化搜索机制，修复在设备模块多的情况下可能搜不完全的Bug。

#### v1.4.2
1:舍弃所有api都需要Context参数，仅保留HDLDeviceManager.init(Context context);具体可查看Demo

2：音乐协议相关api名称修改：舍弃音乐协议多api并存，修改为统一使用HDLaudioCtrl(AppliancesInfo info,int type)、HDLaudioCtrl(AppliancesInfo info,int type,int value)、HDLaudioCtrl(AppliancesInfo info,int type,int value1,int value2)。增加获取下一列表、上一列表api。具体使用查看第8点或查看Demo。


#### v1.4.1
1:更新api相关名称：CommandData更改为HDLCommand，DeviceManager更改为HDLDeviceManager。由于包名的更改，将会导致已集成旧版的项目导包报错，建议直接复制demo导包内容，以及相应修改CommandData、DeviceManager为HDLCommand、HDLDeviceManager。

2:增加音乐协议。功能包括：搜索音乐列表、点播、上一首、下一首、播放/暂停、播放/停止、音量设置、播放模式切换。


#### v1.3.1
1:修复调用搜索api后，立刻调用获取备注api某些情况获取失败Bug

#### v1.3.0
1: 增加HDL报警设备收发。可用EventBus或Broadcast接收。

2: 搜索api修改为：调用搜索获取HDL设备数据、HDL场景数据api，5秒后回调EventBus数据。

3: 去除备注乱码字符。

4：修复灯光设备备注某些网络获取不到的bug


#### v1.2.14
1：SDK的初始化不再包含EventBus的初始化，厂家根据自身情况在需要接收的界面初始化

2：废弃`HDLCommand.devicesSearch(Conetext context);`搜索api，增加区分HDL设备和HDL场景api。`HDLCommand.HDLdevicesSearch(Context context);`和`HDLCommand.HDLscenesSearch(Context context);`


    
    
