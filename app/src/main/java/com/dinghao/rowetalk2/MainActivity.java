package com.dinghao.rowetalk2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dinghao.rowetalk2.util.FileUtil;
import com.dinghao.rowetalk2.util.SimUtil;
import com.dinghao.rowetalk2.util.SmsUtil;
import com.dinghao.rowetalk2.util.WifiUtil;
import com.liblua.LuaEnv;
import com.liblua.LuaScreen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivityttt";
    public static int mPhoneSeq = 0;
    private Button wifiState;
    private Button openWifi;
    private Button connectWifi;
    private Button startTask;
    private Button socketConnect;
    private LinearLayout activityMain;
    private Button readFile;
    private Button switchSim1;
    private Button switchSim2;
    private String mImsi1, mImsi2;
    private Button getImsi;
    private Button traveAll;
    private EditText etIndex;
    private Button sendSms;
    private Button receiveSms;
    private Button startTaskIndex;
    private Button lua;
    private Button email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        etIndex.setText("0");

        Intent service = new Intent(this, MainService.class);
        startService(service);
    }

    private void initView() {
        wifiState = (Button) findViewById(R.id.wifi_state);
        openWifi = (Button) findViewById(R.id.open_wifi);
        connectWifi = (Button) findViewById(R.id.connect_wifi);
        startTask = (Button) findViewById(R.id.start_task);
        socketConnect = (Button) findViewById(R.id.socket_connect);
        activityMain = (LinearLayout) findViewById(R.id.activity_main);

        wifiState.setOnClickListener(this);
        openWifi.setOnClickListener(this);
        connectWifi.setOnClickListener(this);
        startTask.setOnClickListener(this);
        socketConnect.setOnClickListener(this);
        readFile = (Button) findViewById(R.id.read_file);
        readFile.setOnClickListener(this);
        switchSim1 = (Button) findViewById(R.id.switch_sim1);
        switchSim1.setOnClickListener(this);
        switchSim2 = (Button) findViewById(R.id.switch_sim2);
        switchSim2.setOnClickListener(this);
        getImsi = (Button) findViewById(R.id.get_imsi);
        getImsi.setOnClickListener(this);
        traveAll = (Button) findViewById(R.id.trave_all);
        traveAll.setOnClickListener(this);
        etIndex = (EditText) findViewById(R.id.et_index);
        etIndex.setOnClickListener(this);
        sendSms = (Button) findViewById(R.id.send_sms);
        sendSms.setOnClickListener(this);
        receiveSms = (Button) findViewById(R.id.receive_sms);
        receiveSms.setOnClickListener(this);
        startTaskIndex = (Button) findViewById(R.id.start_task_index);
        startTaskIndex.setOnClickListener(this);
        lua = (Button) findViewById(R.id.lua);
        lua.setOnClickListener(this);
        email = (Button) findViewById(R.id.email);
        email.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_file:
                FileUtil.readFile("/mnt/sdcard/rowetalk2/api/wifilist.txt");
                break;
            case R.id.wifi_state:
                //得到wifi状态
                WifiUtil.getWifiState(MainActivity.this);
                break;
            case R.id.open_wifi:
                //打开wifi
                WifiUtil.openWifi(MainActivity.this);
                break;
            case R.id.connect_wifi:
                //连接wifi
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String SSID = "dinghao_asus";
                        String Password = "1qaz2wsx";
                        WifiUtil.connectWifi(SSID, Password, 3);
                    }
                }).start();

                break;
            case R.id.start_task:
                Intent intent = new Intent(this, MainService.class);
                intent.putExtra("index", -1);
                startService(intent);
                break;
            case R.id.start_task_index:
                String index = etIndex.getText().toString().trim();
                if (TextUtils.isEmpty(index)) {
                    Toast.makeText(this, "index不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentIndex = new Intent(this, MainService.class);
                    intentIndex.putExtra("sim1", true);
                    intentIndex.putExtra("index", Integer.parseInt(index));
                    startService(intentIndex);
                }
                break;
            case R.id.socket_connect:
                Socket longSocket = new Socket();
                SocketAddress receiveAddress = new InetSocketAddress("ip", 8080);
                try {
                    longSocket.connect(receiveAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.switch_sim1:
                SimUtil.setTraveAll(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimUtil.switchSimCard(MainActivity.this, true, Integer.parseInt(etIndex.getText().toString()),
                                new SimUtil.OnSimReadyListener() {
                                    @Override
                                    public void onSimReady() {
                                        Log.i(TAG, "switchSimCard: sim卡状态ready");
                                    }
                                });
                    }
                }).start();
                break;
            case R.id.switch_sim2:
                SimUtil.setTraveAll(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimUtil.switchSimCard(MainActivity.this, false, Integer.parseInt(etIndex.getText().toString()),
                                new SimUtil.OnSimReadyListener() {
                                    @Override
                                    public void onSimReady() {
                                        Log.i(TAG, "switchSimCard: sim卡状态ready");
                                    }
                                });
                    }
                }).start();
                break;
            case R.id.get_imsi:
                mImsi1 = SimUtil.getImsi(MainActivity.this, true);
                mImsi2 = SimUtil.getImsi(MainActivity.this, false);
                break;
            case R.id.trave_all:
                SimUtil.setTraveAll(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimUtil.switchSimCard(MainActivity.this, true, 0, new SimUtil.OnSimReadyListener() {
                            @Override
                            public void onSimReady() {
                                Log.i(TAG, "switchSimCard: sim卡状态ready");
                            }
                        });
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimUtil.switchSimCard(MainActivity.this, false, 0, new SimUtil.OnSimReadyListener() {
                            @Override
                            public void onSimReady() {
                                Log.i(TAG, "switchSimCard: sim卡状态ready");
                            }
                        });
                    }
                }).start();
                break;
            case R.id.send_sms:
                SmsUtil.sendSms("18652063209", "信息内容...");
                break;
            case R.id.receive_sms:
                SmsUtil.readSms(MainActivity.this);
                break;
            case R.id.lua:
//                boolean test = LuaEnv.getInstance().doAsset("test.lua");
//                Log.i(TAG, "onClick: "+test);
                Object test = LuaEnv.getInstance().runFunc("test", null);
                Log.i(TAG, "onClick: "+test);
                break;
            case R.id.email:
                LuaScreen luaScreen = new LuaScreen(null, null, null, null);
                luaScreen.findText(30,150,"Button");
                break;
        }
    }

}
