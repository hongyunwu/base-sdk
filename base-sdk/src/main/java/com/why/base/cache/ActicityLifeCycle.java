package com.why.base.cache;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.squareup.leakcanary.RefWatcher;
import com.why.base.utils.IMMLeaks;
import com.why.base.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * Created by wuhongyun on 17-8-28.
 *
 * 可以在此处理一些性能监听，统计事件
 */

class ActicityLifeCycle implements Application.ActivityLifecycleCallbacks {

    private AppCache mAppCache;
    private InputMethodManager inputMethodManager;
    private Field mServedViewField;
    private Field mHField;
    private Method finishInputLockedMethod;
    public ActicityLifeCycle(AppCache appCache) {
        this.mAppCache = appCache;
        initIMM();


    }

    /**
     * 初始化输入法service
     */
    private void initIMM() {
        // Don't know about other versions yet.
        if (SDK_INT < KITKAT || SDK_INT > 22) {
            return;
        }
        inputMethodManager = (InputMethodManager) AppCache.getContext().getSystemService(INPUT_METHOD_SERVICE);
        Method focusInMethod;
        try {
            mServedViewField = InputMethodManager.class.getDeclaredField("mServedView");
            mServedViewField.setAccessible(true);
            mHField = InputMethodManager.class.getDeclaredField("mServedView");
            mHField.setAccessible(true);
            finishInputLockedMethod = InputMethodManager.class.getDeclaredMethod("finishInputLocked");
            finishInputLockedMethod.setAccessible(true);
            focusInMethod = InputMethodManager.class.getDeclaredMethod("focusIn", View.class);
            focusInMethod.setAccessible(true);
        } catch (Exception unexpected) {
            Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
            return;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mAppCache.pushActivity(activity);

        handleLeaks(activity);
    }

    /**
     * 处理掉输入法持有activity引用导致的泄露问题
     *
     * @param activity
     */
    private void handleLeaks(Activity activity) {
        if (SDK_INT < KITKAT || SDK_INT > 22) {
            return;
        }
        try {
            IMMLeaks.ReferenceCleaner cleaner =
                    new IMMLeaks.ReferenceCleaner(inputMethodManager, mHField, mServedViewField,
                            finishInputLockedMethod);
            View rootView = activity.getWindow().getDecorView().getRootView();
            ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalFocusChangeListener(cleaner);
        }catch (Exception e){
            e.printStackTrace();
        }
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
