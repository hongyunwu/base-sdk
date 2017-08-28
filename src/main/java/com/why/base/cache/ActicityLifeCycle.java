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
    private RefWatcher mRefWatcher;

    public ActicityLifeCycle(RefWatcher refWatcher) {
        this.mRefWatcher = refWatcher;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

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
        if (mRefWatcher!=null){
            mRefWatcher.watch(activity);
        }
        LogUtils.i("onActivityDestroyed: " + activity.getClass().getSimpleName());
    }
}
