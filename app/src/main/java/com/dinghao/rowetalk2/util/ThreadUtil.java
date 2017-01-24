package com.dinghao.rowetalk2.util;

public class ThreadUtil {

	public static boolean Sleep(long ms){
		try {
			Thread.sleep(ms);
			return true;
		}catch(Exception e){
			//
		}
		return false;
	}
}
