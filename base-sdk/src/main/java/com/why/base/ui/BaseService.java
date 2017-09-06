package com.why.base.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.why.base.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by wuhongyun on 17-9-6.
 */

public class BaseService extends Service implements Handler.Callback {
    /**
     * 用于处理handler msg
     */
    protected Handler mHandler = new Handler(Looper.getMainLooper(),this);

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean handleMessage(Message msg) {



        return true;
    }

    /**
     * 当前线程的event回调事件
     * @param event 事件需要继承BaseEvent类型
     * @param <E>
     */
    @Subscribe
    public <E extends BaseEvent>void onEventCallBack(E event){

    }
}
