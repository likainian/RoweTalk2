package com.dinghao.rowetalk2;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dinghao.rowetalk2.util.TaskUtil;



/**
 * Created by li on 2016/12/7.
 */

public class MainService extends Service {
    private static final String TAG = "MainServicettt";
    public static int MSG_UPDATE_APP;
    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            //任务
            long nowTime = SystemClock.currentThreadTimeMillis();
            if(nowTime-lastTime>300000){
                //发送邮件

            }
            Log.i("ttt", "run: 执行任务");
            handler.postDelayed(r,300000);
        }
    };
    private long lastTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handler.post(r);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        lastTime = SystemClock.currentThreadTimeMillis();

        if(intent!=null){
            int index = intent.getIntExtra("index", -1);
            boolean sim1 = intent.getBooleanExtra("sim1",true);
            switch(index){
                case -1:
                    Log.i(TAG, "onStartCommand: 开启任务");
                    TaskUtil.startTask();
                    break;
                default:
                    TaskUtil.startTask(sim1,index);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateStatus(String s) {

    }
}
