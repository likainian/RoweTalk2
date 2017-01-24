package com.dinghao.rowetalk2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dinghao.rowetalk2.util.SimUtil;
import com.dinghao.rowetalk2.util.SmsUtil;
import com.dinghao.rowetalk2.util.WifiUtil;

/**
 * Created by li on 2016/12/14.
 */

public class MainReceiver extends BroadcastReceiver {
    private static final String TAG = "MainReceiverttt";
    @Override
    public void onReceive(final Context context, Intent intent) {
        switch(intent.getAction()){
            case "android.intent.action.BOOT_COMPLETED":
                Intent service = new Intent(context,MainService.class);
                context.startService(service);
                Log.i(TAG, "开机自动服务自动启动.....");
                break;
            case "rowetalk2.intent.action.sim_all":
                SimUtil.setTraveAll(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimUtil.switchSimCard(context, true, 0, new SimUtil.OnSimReadyListener() {
                            @Override
                            public void onSimReady() {
                                Log.i(TAG, "switchSimCard: sim卡状态ready");
                            }
                        });
                    }
                }).start();
                break;
            case "rowetalk2.intent.action.wifi_state":
                WifiUtil.getWifiState(context);
                Log.i(TAG, "onReceive: WiFI状态");
                break;
            case "rowetalk2.intent.action.wifi_start":
                WifiUtil.openWifi(context);
                Log.i(TAG, "onReceive: 打开WiFI");
                break;
            case "rowetalk2.intent.action.wifi_connect":
                String ssid = intent.getStringExtra("ssid");
                String password = intent.getStringExtra("password");
                int type = 3;
                if(password == ""){
                    type = 1;
                }
                WifiUtil.connectWifi(ssid , password , type);
                Log.i(TAG, "onReceive: 打开WiFI");
                break;
            case "rowetalk2.intent.action.start_task":
                intent.setClass(context,MainService.class);
                intent.putExtra("sim1",true);
                intent.putExtra("index",-1);
                context.startService(intent);
                break;
            case "rowetalk2.intent.action.send_sms":
                //发送短信
                String number = intent.getStringExtra("number");
                String message = intent.getStringExtra("message");
                SmsUtil.sendSms(number,message);
                break;
            case "android.provider.Telephony.SMS_RECEIVED":
                //接受到短信的广播
                Log.i(TAG, "onReceive: 收到广播");
                SmsUtil.receiveSms(intent);
                break;
        }

    }
}
