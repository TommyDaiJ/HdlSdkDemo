package com.hdl.xw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class HDLBroacastRv extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String warningType = intent.getStringExtra("HdlWarning");
        Toast.makeText(context,warningType,Toast.LENGTH_SHORT).show();
    }
}
