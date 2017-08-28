package com.why.base.cache;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.squareup.leakcanary.RefWatcher;
import com.why.base.utils.LogUtils;

/**
 * Created by wuhongyun on 17-8-28.
 *
 * 可以在此处理一些性能监听，统计事件
 */

class ActicityLifeCycle implements Application.ActivityLifecycleCallbacks {

    private AppCache mAppCache;

    public ActicityLifeCycle(AppCache appCache) {
        this.mAppCache = appCache;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mAppCache.pushActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        RefWatcher mRefWatcher = mAppCache.getRefWatcher();
        if (mRefWatcher!=null){
            mRefWatcher.watch(activity);
        }
        mAppCache.popActivity(activity);
        LogUtils.i("onActivityDestroyed: " + activity.getClass().getSimpleName());
    }
}
