package com.hdl.xw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hdl.libr.hdl_lib.HDLAppliances.HDLAudio.HDLAudio;
import com.hdl.libr.hdl_lib.HDLCommand;
import com.hdl.libr.hdl_lib.HDLDeviceManager.Bean.AppliancesInfo;
import com.hdl.libr.hdl_lib.HDLDeviceManager.EventBusEvent.HDLAudioInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity {
    private Button getCurSongInfoBtn,playPauseBtn,playStopBtn,preSongBtn,nextSongBtn,volMinBtn,volMidBtn,volMaxBtn,modeBtn,nextListBtn,preListBtn;
    private AppliancesInfo appliancesInfo;
    private List<String> listString = new ArrayList<>() ;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private TextView curSongInfoTv,curSongNameTv;
    private int curListNum ;//由于不同厂商需求，必须自行记录当前的列表号
    private boolean isInit ;//记录是否初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        curSongNameTv = (TextView) findViewById(R.id.curSongName);
        curSongInfoTv = (TextView) findViewById(R.id.curSongInfo);
        getCurSongInfoBtn = (Button) findViewById(R.id.current);
        playPauseBtn = (Button) findViewById(R.id.playpause);
        playStopBtn = (Button) findViewById(R.id.playstop);
        preSongBtn = (Button) findViewById(R.id.pre);
        nextSongBtn = (Button) findViewById(R.id.next);
        volMinBtn = (Button) findViewById(R.id.volmin);
        volMidBtn = (Button) findViewById(R.id.volmid);
        volMaxBtn = (Button) findViewById(R.id.volmax);
        modeBtn = (Button) findViewById(R.id.audiomode);
        nextListBtn = (Button) findViewById(R.id.nextlist);
        preListBtn = (Button) findViewById(R.id.prelist);
        listView=(ListView)findViewById(R.id.audiolist);
        isInit = true;
        listString.add("这个列表显示歌曲");
        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("hdl");
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        adapter=new ArrayAdapter<String>(AudioActivity.this,android.R.layout.simple_list_item_1,listString);
        listView.setAdapter(adapter);
        HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.GET_AUDIO_CURRRENT_INFO);//获取当前音乐信息。返回当前歌曲、所有信息。
        HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.GET_AUDIO_MODE);//获取当前音乐播放模式。仅返回单曲播放等播放模式。
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_CHOOSE_PLAY_SONG,curListNum,position);
            }
        });

        getCurSongInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.GET_AUDIO_CURRRENT_INFO);
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_PLAYPAUSE);
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_PLAYSTOP);
            }
        });

        preSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_PRE_SONG);
            }
        });

        nextSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_NEXT_SONG);
            }
        });

        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_MODE_UP);//播放模式+
//                HDLCommand.HDLaudioCtrl(AudioActivity.this,appliancesInfo,HDLAudio.SET_AUDIO_MODE_DOWN);//播放模式-
            }
        });

        volMinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_VOL,0);//音量最小：0。小于0，SDK不处理
            }
        });

        volMidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_VOL,40);
            }
        });

        volMaxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_AUDIO_VOL,79);//音量最大：79。超过79，SDK不处理
            }
        });

        nextListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_NEXT_LIST);//获取下一列表，当前音乐会停止播放
            }
        });

        preListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.SET_PRE_LIST);//获取上一列表，当前音乐会停止播放
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioEventMain(HDLAudioInfoEvent event){
        //判断是否为本音乐模块的子网号，设备号
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
                    if(isInit){
                        isInit = false;//此操作为仅初始化才请求获取当前音乐列表，厂商可以自行决定何时获取音乐列表
                        HDLCommand.HDLaudioCtrl(appliancesInfo,HDLAudio.GET_AUDIO_LIST,curListNum);//获取当前播放列表，此方法如果在歌曲播放状态时调用则会导致歌曲停止播放，硬件设计如此
                    }
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


    }
}
