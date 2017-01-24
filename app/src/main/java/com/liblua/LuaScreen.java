package com.liblua;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dinghao.rowetalk2.application.BaseApplication;
import com.luajava.JavaFunction;
import com.luajava.LuaException;
import com.luajava.LuaState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/1/19.
 */

public class LuaScreen extends JavaFunction {
    private static final String TAG = "LuaScreenttt";
    private LuaEnv Env;
    private Context context;
    private LuaState L;
    private String attr;
    private List<AccessibilityNodeInfo> list = new ArrayList<>();

    public LuaScreen(LuaState L, LuaEnv env, Context context, LuaState l) {
        super(L);
        Env = env;
        this.context = context;
        this.L = l;
    }

    // 参数：int x, int y, String name
    // 结果：boolean result,String errorCode, String attr//返回值会有延迟
    @Override
    public int execute() throws LuaException {
        if (L.getTop() < 4)
        {
            L.pushBoolean(false);
            L.pushString("bad parameter count");
            return 2;
        }
        int x = (int)L.toInteger(2);
        int y = (int)L.toInteger(3);
        String name = L.toString(4);

        if(name == null){
            L.pushBoolean(false);
            L.pushString("name is null");
            return 2;
        }
        if(!findText(x,y,name)){
            L.pushBoolean(false);
            L.pushString("screen failed.");
            return 2;
        }
        L.pushBoolean(true);
        L.pushString("success.");
        L.pushString(attr);
        return 3;
    }

    public boolean findText(int x, int y, String name) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        AccessibilityNodeInfo info = application.getInfo();
        if(info!=null){
            recycle(info);
            for (int i = 0; i < list.size(); i++) {
                Rect rect = new Rect();
                list.get(i).getBoundsInScreen(rect);
                if(list.get(i).getText()!=null&&list.get(i).getText().toString().contains(name)&&rect.contains(x,y)){
                    Log.i(TAG, "lookFor: "+list.get(i).getText().toString().contains(name)+rect.contains(x,y)+rect.toString());
                    return true;
                }
            }

        }
        return false;
    }
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "getRootInActiveWindow：" + info.toString());
            list.add(info);
        } else {
            Log.i(TAG, "getRootInActiveWindow：" + info.toString());
            list.add(info);
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }
}
