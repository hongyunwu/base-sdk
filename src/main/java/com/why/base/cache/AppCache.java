package com.why.base.cache;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by wuhongyun on 17-8-28.
 * 用于应用的全局缓存,全局缓存信息：1.用户登陆信息，2.activity栈信息，3.
 */

public class AppCache {
    private Context mContext;
    private RefWatcher mRefWatcher;

    /**
     * 私有化构造函数
     */
    private AppCache(){
    }

    private static class SingleCacheHolder{
        private static AppCache mAppCache = new AppCache();
    }

    /**
     * 单例获取
     * @return
     */
    private static AppCache getInstance(){
        return SingleCacheHolder.mAppCache;
    }

    public static void init(Application application){
        getInstance().onInit(application);
    }
    
    /**
     * 初始化操作
     * @param application
     */
    private void onInit(Application application) {
        mContext = application.getApplicationContext();
        initLeakCanary(application);
        application.registerActivityLifecycleCallbacks(new ActicityLifeCycle(getRefWatcher()));
    }

    /**
     * 获取内存泄露的观察者
     *
     * @return
     */
    public static RefWatcher getRefWatcher() {
        return getInstance().mRefWatcher;
    }

    /**
     * 在debug环境预装内存泄露检测工具
     *
     * @param application
     */
    private void initLeakCanary(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }
        mRefWatcher = LeakCanary.install(application);
    }

}
