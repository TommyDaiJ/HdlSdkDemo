package com.hdl.xw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//此接收广播作用为全局接收 HDL 警报信息。如果没有必要可不接收
public class HDLBroacastRv extends BroadcastReceiver {
    String HDL_WARNING = "HdlWarning";

    @Override
    public void onReceive(Context context, Intent intent) {
        String warningType = intent.getStringExtra(HDL_WARNING);//这是警报广播的String标志
        Toast.makeText(context,warningType,Toast.LENGTH_SHORT).show();
    }
}
