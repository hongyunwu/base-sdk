package com.why.base;

import android.app.Application;

import com.why.base.cache.AppCache;
import com.why.base.utils.LogUtils;

/**
 * Created by wuhongyun on 17-8-28.
 * 可以选择继承BaseApplication或者重写AppCache.init(this)来使用此框架
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCache.init(this);
        LogUtils.i("onCreate...");

    }
}
