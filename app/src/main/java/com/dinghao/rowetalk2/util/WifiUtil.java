package com.dinghao.rowetalk2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dinghao.rowetalk2.bean.WifiBean;

import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class WifiUtil {
	private static WifiManager mWifiManager;
	private static List<ScanResult> mWifiList;
	private static String TAG = "WifiUtilttt";
	//判断wifi打开状态，判断wifi连接状态，把wifilist写入文件
	public static void getWifiState(Context context){
		if(mWifiManager==null){
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        WifiBean wifiBean = new WifiBean();
        String json = null;
        if(!mWifiManager.isWifiEnabled()){
			Log.i("WifiUtilttt", "wifi未打开");
            wifiBean.setOpen(false);
		}else {
			Log.i(TAG, "getWifiState: wifi已打开");
			wifiBean.setOpen(true);
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWifi.isConnected()) {
				wifiBean.setConnect(true);
                wifiBean.setNetworkInfo(mWifi);
			}else {
                wifiBean.setConnect(false);
            }
			// 得到扫描结果
			mWifiManager.startScan();
			mWifiList = mWifiManager.getScanResults();
            wifiBean.setmWifiList(mWifiList);
        }
//        json = new Fastjs().toJson(wifiBean);
		json = JSON.toJSONString(wifiBean);
        Log.i(TAG, "getWifiState: "+json);
		FileUtil.saveFile(json,"/mnt/sdcard/rowetalk2/api/", "wifilist.txt", false);
		Log.i("WifiUtilttt", "wifi写入");
	}
	//打开wifi
	public static void openWifi(final Context context){
		if(mWifiManager==null){
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);  
        }
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
				getWifiState(context);
			}
		}).start();
	}
	//连接指定wifi
	public static void connectWifi(String ssid, String password, int Type){
		Log.i(TAG , "SSID:" + ssid + ",password:" + password);
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + ssid + "\"";

		WifiConfiguration tempConfig = IsExist(ssid);

		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		} else {
			Log.i(TAG, "IsExists is null.");
		}

		if (Type == 1){
			Log.i(TAG, "Type =1.");
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2){
			Log.i(TAG, "Type =2.");
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3){

			Log.i(TAG, "Type =3.");
			config.preSharedKey = "\"" + password + "\"";

			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		mWifiManager.addNetwork(config);
	}
	// 查看以前是否已经配置过该SSID
	private static WifiConfiguration IsExist(String ssid) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
}
