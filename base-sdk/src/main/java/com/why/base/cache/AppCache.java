package com.why.base.cache;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.why.base.executor.ThreadManager;

import java.util.ArrayList;

/**
 * Created by wuhongyun on 17-8-28.
 * 用于应用的全局缓存,全局缓存信息：1.用户登陆信息，2.activity栈信息，3.异常全局捕捉
 */

public class AppCache {
    private Application mApplication;
    private RefWatcher mRefWatcher;
    private ActicityLifeCycle mActicityLifeCycle;

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
        this.mApplication = application;
        CrashHandler.init(application);//全局捕获异常
        initLeakCanary(application);//内存泄露检查
        mActicityLifeCycle = new ActicityLifeCycle(this);
        application.registerActivityLifecycleCallbacks(mActicityLifeCycle);//用于内存检测

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


    private ArrayList<Activity> activities = new ArrayList<>();
    /**
     * 把activity加入堆栈集合中，方便统一退出
     * @param activity
     */
    synchronized void pushActivity(Activity activity) {

        if (!activities.contains(activity)){
            activities.add(activity);
        }

    }

    /**
     * activity从堆栈集合中弹出
     * @param activity
     */
    synchronized void popActivity(Activity activity) {
        if (activities.contains(activity)){
            activities.remove(activity);
        }
    }

    /**
     * 退出app,安全退出
     */
    synchronized void exit(){

        for (Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
        if (mApplication!=null){
            mApplication.unregisterActivityLifecycleCallbacks(mActicityLifeCycle);
        }

        android.os.Process.killProcess(android.os.Process.myPid());
        //线程管理类关闭线程
        ThreadManager.getInstance().getDefaultPool().shutDownNow();
        System.exit(1);

    }

    /**
     * 静态调用退出应用程序
     */
    public static void exitApp(){
        getInstance().exit();
    }

    /**
     * 返回的是applicationContext对象
     * @return
     */
    public static Context getContext(){

        return getInstance().mApplication.getApplicationContext();
    }

}
