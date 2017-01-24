package com.dinghao.rowetalk2.util;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.gpio.GpioManager;
import android.hardware.gpio.GpioPort;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.io.IOException;


/**
 * Created by li on 2016/12/16.
 */

public class SimUtil {
    private static final String TAG = "SimUtilttt";
    private static boolean traveAll;
    private static boolean isSwitching;
    private static TelephonyManager mTelManager;
    private static GpioPort mGpioPort;
    private static GpioManager mGpioManager;
    private static ITelephony mTelephony;
    private static String[] sim1_slot_gpios = {
            "GPIO42", "GPIO43", "GPIO19"
    };
    private static String[] sim1_sim_gpios = {
            "GPIO5", "GPIO11", "GPIO81", "GPIO82"
    };
    private static SharedPreferences sp;

    private static String[] sim2_slot_gpios = {
            "GPIO0", "GPIO1", "GPIO2"
    };
    private static String[] sim2_sim_gpios = {
            "GPIO61", "GPIO80", "GPIO78", "GPIO79"
    };

    private static int[] sim_slot_index_table = {
            7,//0
            6,//1
            5,//2
            4,//3
            3,//4
            2,//5
            1,//6
            0,//7
    };
    private static int[] sim_sim_index_table = {
            7,//0
            6,//1
            5,//2
            4,//3
            11,//4
            10,//5,
            9,//6
            8,//7
            12,//8
            13,//9
            14,//10
            15,//11
            0,//12
            1,//13
            2,//14
            3,//15
    };

    public static void setTraveAll(boolean traveAll) {
        SimUtil.traveAll = traveAll;
    }

    public static String getImsi(Context context, boolean sim1){
        if (mTelManager == null) {
            mTelManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        boolean b = isSimInsert(sim1);
        String imsi = null;
        int subId = sim1?getSubId(0):getSubId(1);
        int subId2 = getSubId(sim1?0:1);
        Log.e(TAG, "getImsi: "+(sim1?"sim1":"sim2")+", insert: "+b+", subId: "+subId+", "+subId2);
        if(mTelManager != null && b){
            imsi = mTelManager.getSubscriberId(subId2);
        }
        Log.e(TAG, "getImsi: "+(sim1?"sim1":"sim2")+", imsi: "+imsi);
        return imsi;
    }

    public static int getSubId(int slotId){
        int[] subId = new int[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            subId = SubscriptionManager.getSubId(slotId);
        }
        if (subId != null && subId.length > 0) {
            return subId[0];
        }
        return 0;
    }

    public static boolean isSimInsert(boolean sim1){
        if(mTelephony==null){
            mTelephony = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
        }
        if (mTelephony != null) {
            try {
                return mTelephony.hasIccCardUsingSlotId(sim1?0:1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    private static Object lock = new Object();
    public static void setGpioOutput(String gpio, int value)
    {
        try {
            mGpioPort = mGpioManager.obtainGpioPortControl(gpio);
            if (mGpioPort != null) {
                int gpio_num = mGpioManager.getGpioValue(gpio);
                Log.i(TAG, "cxw--getGpioValue=" + gpio_num + "gpio string=" + gpio);
                mGpioPort.set_mode(gpio_num,0);
                mGpioPort.set_dir(gpio_num,1);
                //mGpioPort.set_pullen(gpio_num,1);
                //mGpioPort.set_pull(gpio_num,1);
                mGpioPort.set_data(gpio_num,value);
                Log.e(TAG, "set " + gpio + " " +value + " success");
            }
            else
                Log.e(TAG, "set " + gpio + " " +value + " fail");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void set_sim_gpio(boolean sim1, int index){
        int slot_index = index/16;
        int sim_index = index%16;
        if(sim1) {
            int slot = sim_slot_index_table[slot_index];
            for (int i = 0; i < 3; i++) {
                setGpioOutput(sim1_slot_gpios[i], (slot >> i) & 0x1);
            }
            setGpioOutput("GPIO4", 1);//G2A Low
            int sim = sim_sim_index_table[sim_index];
            for (int i = 0; i < 4; i++) {
                setGpioOutput(sim1_sim_gpios[i], (sim >> i) & 0x1);
            }
        }else{
            int slot = sim_slot_index_table[slot_index];
            for (int i = 0; i < 3; i++) {
                setGpioOutput(sim2_slot_gpios[i], (slot >> i) & 0x1);
            }
            setGpioOutput("GPIO3", 1);//G2A Low
            int sim = sim_sim_index_table[sim_index];
            for (int i = 0; i < 4; i++) {
                setGpioOutput(sim2_sim_gpios[i], (sim >> i) & 0x1);
            }
        }
    }
    public interface OnSimReadyListener{
        void onSimReady();
    }
    public static void switchSimCard(Context context,boolean sim1, int index,OnSimReadyListener listener){
        isSwitching = true;
        long start = SystemClock.currentThreadTimeMillis();
        if(mGpioManager==null){
            mGpioManager = (GpioManager)context.getSystemService(Context.GPIO_SERVICE);
        }
        String[] ports = mGpioManager.getSupportedGpioPorts();
        if (ports != null && ports.length > 0) {
            //SIM1
            if(sim1) {
                setGpioOutput("GPIO86",0);
                synchronized (lock) {
                    try{
                        lock.wait(2000);
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                }
                set_sim_gpio(true, index);
                synchronized (lock) {
                    try{
                        lock.wait(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                }
                setGpioOutput("GPIO86",1);
                synchronized (lock) {
                    try{
                        lock.wait(4000);
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                }
            }else {
                setGpioOutput("GPIO83",0);
                synchronized (lock) {
                    try{
                        lock.wait(2000);
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                }
                set_sim_gpio(false, index);
                synchronized (lock) {
                    try{
                        lock.wait(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                }
                setGpioOutput("GPIO83",1);
                synchronized (lock) {
                    try{
                        lock.wait(4000);
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }
                }
            }
            long end = SystemClock.currentThreadTimeMillis();
            String imsi = getImsi(context,sim1);
            int num = 0;
            while (imsi==null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "switchSimCard: sleep");
                imsi = getImsi(context,sim1);
                num++;
                if(num>5){
                    break;
                }
            }
            if(imsi!=null){
                sp = context.getSharedPreferences("sim",Context.MODE_PRIVATE);
                if(sp.getString(index+"@"+sim1,"").equals("")||!sp.getString(index+"@"+sim1,"").equals(imsi+"@"+readSIMCard(context))){
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(index+"@"+sim1,imsi+"@"+readSIMCard(context));
                    edit.commit();
                }
                if(getSimState(context)==TelephonyManager.SIM_STATE_READY){
                    listener.onSimReady();
                }
                Log.e(TAG, "switchSimCard: switchSimCard is success! use time:"+(end-start)+" index:"+index);
                switchNextSim(context,sim1,index,listener);
            }else {
                if(isSimInsert(sim1)){
                    switchSimCard(context,sim1,index,listener);
                }else {
                    Log.e(TAG, "switchSimCard: switchSimCard is failed! use time:"+(end-start)+" index:"+index);
                    switchNextSim(context,sim1,index,listener);
                }
            }


        }else{
            isSwitching = false;
            Log.e(TAG, "switchSimCard: switchSimCard is failed. ports == null");
        }
    }
    public static void switchNextSim(Context context,boolean sim1,int index,OnSimReadyListener listener){
        if(traveAll){
            if(index<127){
                switchSimCard(context,sim1,++index,listener);
            }else {
                isSwitching = false;
                Log.e(TAG, "switchSimCard: traveAll is success");
            }
        }else {
            isSwitching = false;
        }
    }
    public static int getSimState(Context context) {
        // TODO Auto-generated method stub
        return ((TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE)).getSimState();
    }
    public static String readSIMCard(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuffer sb = new StringBuffer();
        switch(tm.getSimState()){ //getSimState()取得sim的状态 有下面6中状态
            case TelephonyManager.SIM_STATE_ABSENT :sb.append("无卡");break;
            case TelephonyManager.SIM_STATE_UNKNOWN :sb.append("未知状态");break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED :sb.append("需要NetworkPIN解锁");break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED :sb.append("需要PIN解锁");break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED :sb.append("需要PUK解锁");break;
            case TelephonyManager.SIM_STATE_READY :sb.append("良好");break;
        }

        if(tm.getSimSerialNumber()!=null){
            sb.append("@" + tm.getSimSerialNumber().toString());
        }else{
            sb.append("@无法取得SIM卡号");
        }

        if(tm.getSimOperator().equals("")){
            sb.append("@无法取得供货商代码");
        }else{
            sb.append("@" + tm.getSimOperator().toString());
        }

        if(tm.getSimOperatorName().equals("")){
            sb.append("@无法取得供货商");
        }else{
            sb.append("@" + tm.getSimOperatorName().toString());
        }

        if(tm.getSimCountryIso().equals("")){
            sb.append("@无法取得国籍");
        }else{
            sb.append("@" + tm.getSimCountryIso().toString());
        }

        if (tm.getNetworkOperator().equals("")) {
            sb.append("@无法取得网络运营商");
        } else {
            sb.append("@" + tm.getNetworkOperator());
        }
        if (tm.getNetworkOperatorName().equals("")) {
            sb.append("@无法取得网络运营商名称");
        } else {
            sb.append("@" + tm.getNetworkOperatorName());
        }
        if (tm.getNetworkType() == 0) {
            sb.append("@无法取得网络类型");
        } else {
            sb.append("@" + tm.getNetworkType());
        }
        return sb.toString();
    }
}
