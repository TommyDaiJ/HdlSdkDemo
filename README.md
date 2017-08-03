# HdlSdkDemo

This demo demonstrates how to use the HDL SDK


# HDL SDK通讯协议文档

   此SDK仅针对安卓平台进行集成，旨在集成HDL SDK后，可调用相关API，实现HDL设备的搜索、控制、获取当前状态等。在文档最后会提供demo示例，详情请查看demo。以下详细列出HDL SDK集成的相关信息：
   
## 1：平台条件

1.1 目前仅支持Android开发平台，Android SDK 版本4.2以上。

1.2 目前支持Android Studio IDE集成，通过依赖 compile ‘com.hdl.lib:hdllib:1.2.1’ 即可成功将HDL SDK集成到项目中。（由于Bintay方面还在审核1.1.1版本，存在依赖不成功的可能，若不成功请依赖1.2.0，但建议依赖最新的版本）。

1.3 Android Studio平台也支持提供arr包依赖方式，此种方式可随时拿到最新的SDK版本。

1.4 支持Eclipse 安卓开发平台，此种方式可提供jar包依赖，由于SDK有依赖其他第三方库，存在此平台支持不理想的情况，建议转到1.2或1.3方式。

1.5 调试SDK建议使用真机调试，模拟器可能会导致一些不知名的问题。

## 2：SDK初始化

2.1 在build.gradle文件上依赖相应的库

2.1.1 `compile(name: 'hdl_lib-v1.2.1', ext: 'aar')`此种方式依赖为aar文件依赖，此aar包为HDL Lib的通讯包。可向相关开发人员索取最新aar包，`compile 'com.hdl.lib:hdllib:1.2.0'`此种方式与aar包依赖方式同样效果（详情请看demo）

2.1.2   `compile 'org.greenrobot:eventbus:3.0.0' ` 这个依赖包为接收HDL Lib的EventBusEvent事件，必须依赖才能接收。（详情请看demo）

2.1.3  `compile 'com.squareup.okhttp3:okhttp:3.4.1'`这个为接收On设备的okhttp包，非必须依赖，若要集成On设备获取api则必须依赖。

2.2 在需要调用的activity中做初始化操作：`DeviceManager.init(this);`（此操作已经初始化EventBus，具体请查看demo）

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

6.2.1 调`CommandData.curtainCtrl(Context context, AppliancesInfo info, int state)`前两个参数为固定参数，跟6.1.1雷同。第三个参数为：`CurtainCtrlParser.curtainPause`（窗帘停）或`CurtainCtrlParser.curtainOn`（窗帘开）或`CurtainCtrlParser.curtainOff`（窗帘关） 中的一个。

6.2.2 需要接收EventBus的控制返回结果，具体查看demo。仅将`lightCtrlBackInfo`改为`CurtainCtrlBackInfo`

### 6.3 空调控制

6.3.1调用`CommandData.airCtrl(Context context, AppliancesInfo info, int type,int state)`或`CommandData.airCtrl(Context context, AppliancesInfo info, int type)`第一、二个参数跟6.1.1雷同。第三个参数type为：`AirCtrlParser.airOn`，如图这些参数

	public class AirCtrlParser {
    public static final int airOn = 0;
    public static final int airOff = 1;//空调关

    public static final int refTem = 2;//制冷温度

    public static final int airSpeedAuto = 3;//风速自动
    public static final int airSpeedHigh = 4;//风速高风
    public static final int airSpeedMid = 5;//风速中风
    public static final int airSpeedLow = 6;//风速低风

    public static final int airModeRefTem = 7;//空调模式制冷
    public static final int airModeHeatTem = 8;//空调模式制热
    public static final int airModeVen = 9;//空调模式通风
    public static final int airModeAuto = 10;//空调模式自动
    public static final int airModeDehum = 11;//空调模式抽湿

    public static final int heatTem = 12;//制热温度
    public static final int autoTem = 13;//自动温度
    public static final int upTem = 14;//上升温度
    public static final int downTem = 15;//下降温度
    
注意！第四个参数为可选参数，若type为airSpeedHigh、airModeRefTem、airModeHeatTem中的一个需要填state参数，范围为0-84，请按实际情况传参数。


### 6.4 逻辑模块控制
6.4.1调用CommandData.logicCtrl(CtrlActivity.this,appliancesInfo);具体查看demo

# 7 On设备数据获取
7.1 调用`OnManager.getOnDevicesData("192.168.2.113");`参数填写On设备上分享的ip地址。

# 8 向往专用接口

8.1 调用Command.xwSendData(Context context,int subnetID,int deeviceID,int port)  第一个参数：上下文，第二个参数：子网ID , 第三个参数：设备Id,第四个参数：端口号

# 9 Demo下载链接 ：
[HDL SDK Demo](https://github.com/TommyDaiJ/HdlSdkDemo)
    
    
