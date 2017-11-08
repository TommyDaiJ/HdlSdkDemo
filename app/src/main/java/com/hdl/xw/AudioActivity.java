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
    private Button getBtn,playPauseBtn,playStopBtn,preBtn,nextBtn,volMinBtn,volMidBtn,volMaxBtn,modeBtn;
    private AppliancesInfo appliancesInfo;
    private List<String> listString = new ArrayList<>() ;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private TextView curSongInfoTv,curSongNameTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        curSongNameTv = (TextView) findViewById(R.id.curSongName);
        curSongInfoTv = (TextView) findViewById(R.id.curSongInfo);
        getBtn = (Button) findViewById(R.id.get);
        playPauseBtn = (Button) findViewById(R.id.playpause);
        playStopBtn = (Button) findViewById(R.id.playstop);
        preBtn = (Button) findViewById(R.id.pre);
        nextBtn = (Button) findViewById(R.id.next);
        volMinBtn = (Button) findViewById(R.id.volmin);
        volMidBtn = (Button) findViewById(R.id.volmid);
        volMaxBtn = (Button) findViewById(R.id.volmax);
        modeBtn = (Button) findViewById(R.id.audiomode);
        listView=(ListView)findViewById(R.id.audiolist);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        listString.add("这个列表显示歌曲");
        appliancesInfo = (AppliancesInfo) getIntent().getSerializableExtra("hdl");
        HDLCommand.getAudioMode(AudioActivity.this,appliancesInfo);
        adapter=new ArrayAdapter<String>(AudioActivity.this,android.R.layout.simple_list_item_1,listString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HDLCommand.chooseSongNum(AudioActivity.this,appliancesInfo,position);
            }
        });

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.getAudioList(AudioActivity.this,appliancesInfo);
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setAudioPlayPause(AudioActivity.this,appliancesInfo);
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setAudioPlayStop(AudioActivity.this,appliancesInfo);
            }
        });

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setPreSong(AudioActivity.this,appliancesInfo);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setNextSong(AudioActivity.this,appliancesInfo);
            }
        });

        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setAudioModeUp(AudioActivity.this,appliancesInfo);
            }
        });

        volMinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setAudioVol(AudioActivity.this,appliancesInfo,0);
            }
        });

        volMidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setAudioVol(AudioActivity.this,appliancesInfo,40);
            }
        });

        volMaxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HDLCommand.setAudioVol(AudioActivity.this,appliancesInfo,79);
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

        switch (event.getType()){
            case HDLAudio.SONG_NAME_LIST:
                listString.clear();
//                listString = event.getSongNameList();
                for(int i=0;i<event.getSongNameList().size();i++){
                    listString.add(event.getSongNameList().get(i));
                }

                adapter.notifyDataSetChanged();
                break;
            case HDLAudio.CURRENT_VOLUME:
                Log.i("hdl","当前音量值："+event.getAudioInfoInt());
                break;
            case HDLAudio.AUDIO_LIST_NUM:
                int[] ListNum = event.getAudioListInfo();
                Log.i("hdl","当前列表号："+ListNum[0]+" 当前共有列表数："+ListNum[1]);
                break;
            case HDLAudio.CURRENT_LIST_NAME:
                Log.i("hdl","当前列表名："+event.getAudioInfoStr());
                break;
            case HDLAudio.CURRENT_SONG_NUM:
                int[] songNum = event.getAudioListInfo();
                Log.i("hdl","当前歌曲号："+songNum[0]+" 当前共有歌曲数："+songNum[1]);
                break;
            case HDLAudio.CURRENT_SONG_NAME:
                curSongNameTv.setText("当前歌曲名："+event.getAudioInfoStr());
                break;
            case HDLAudio.CURRENT_SONG_Info:
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
                curSongInfoTv.setText("当前歌曲总时长："+songInfo[0]+"秒 ，当前歌曲已播放时长："+songInfo[1]+"秒， 当前歌曲状态："+curStatus);
                break;
            case HDLAudio.CURRENT_MODE:
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

