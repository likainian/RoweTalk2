package com.dinghao.rowetalk2.bean;

import android.net.NetworkInfo;
import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by li on 2016/12/16.
 */

public class WifiBean {
    private boolean open;
    private boolean connect;
    private String type;
    private NetworkInfo networkInfo;
    private List<ScanResult> mWifiList;

    public WifiBean() {
    }

    public WifiBean(boolean open, boolean connect, String type, NetworkInfo networkInfo, List<ScanResult> mWifiList) {
        this.open = open;
        this.connect = connect;
        this.type = type;
        this.networkInfo = networkInfo;
        this.mWifiList = mWifiList;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public List<ScanResult> getmWifiList() {
        return mWifiList;
    }

    public void setmWifiList(List<ScanResult> mWifiList) {
        this.mWifiList = mWifiList;
    }
}
