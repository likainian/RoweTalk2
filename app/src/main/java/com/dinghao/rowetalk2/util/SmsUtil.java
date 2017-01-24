package com.dinghao.rowetalk2.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by li on 2016/12/27.
 */

public class SmsUtil {
    private static final String TAG = "SmsUtilttt";
    private static ContentResolver cr;
    public static void readSms(Context context){
        //读取短信记录
        cr = context.getContentResolver();
        Cursor sms = cr.query(Uri.parse("content://sms"), null, null, null, null);
        while(sms.moveToNext()) {
            String mess  = sms.getString(sms.getColumnIndex("body"));  //短信内容
            String number = sms.getString(sms.getColumnIndex("address"));
            String date = sms.getString(sms.getColumnIndex("date"));
            int type = sms.getInt(sms.getColumnIndex("type"));
            int read = sms.getInt(sms.getColumnIndex("read"));

            Log.i(TAG, "短信为： "+number+" : "+mess);
            Log.i(TAG, "日期为： "+date);
            switch (type) {
                case 1:
                    Log.i(TAG, "本条短信为接收短息");
                    break;
                case 2:
                    Log.i(TAG, "本条短信为发出的短信");
                    break;
            }
            switch (read) {
                case 0:
                    Log.i(TAG, "本条短信未读");
                    break;
                case 1:
                    Log.i(TAG, "本条短信已读");
                    break;
            }
            Log.i(TAG, "-----------------------------------------------");
        }
    }
    public static void sendSms(String phoneNumber,String message){
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        Log.i(TAG, "sendSms: "+smsManager+phoneNumber+message);
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }
    public static void receiveSms(Intent intent){
        Bundle bundle = intent.getExtras();
        SmsMessage msg;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                String phoneNumber = msg.getOriginatingAddress();
                String body = msg.getDisplayMessageBody();
                long time = msg.getTimestampMillis();
                //在这里写自己的逻辑
                Log.i(TAG, "onReceive: "+receiveTime+phoneNumber+body+time);
            }
        }
    }
}
