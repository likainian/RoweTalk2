package com.dinghao.rowetalk2.application;

import android.app.Application;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dinghao.rowetalk2.util.Config;
import com.dinghao.rowetalk2.util.CrashHandler;
import com.liblua.LuaEnv;

public class BaseApplication extends Application {
	private AccessibilityNodeInfo info;

	public AccessibilityNodeInfo getInfo() {
		return info;
	}

	public void setInfo(AccessibilityNodeInfo info) {
		this.info = info;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Config.init(this);
		LuaEnv.init(this);
		CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());  
        Thread.setDefaultUncaughtExceptionHandler(handler);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
