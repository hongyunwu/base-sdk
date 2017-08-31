package com.why.base.executor;

/**
 * Created by wuhongyun on 17-8-30.
 *
 * 线程管理类
 *
 * 使用指南：
 */

public class ThreadManager {

    private ThreadPoolProxy defaultThreadPool;

    /**
     * 构造方法，此处创建
     */
    private ThreadManager(){
    }
    private static ThreadManager mThreadManager;

    public static ThreadManager getInstance(){
        if (mThreadManager==null){
            synchronized (ThreadManager.class){
                if (mThreadManager==null){
                    mThreadManager = new ThreadManager();
                }
            }
        }
        return mThreadManager;
    }

    public ThreadPoolProxy getDefaultPool(){

        if (defaultThreadPool==null) {
            synchronized (ThreadManager.class){
                if (defaultThreadPool==null){
                    defaultThreadPool = new ThreadPoolProxy();
                }
            }
        }
        return defaultThreadPool;
    }



}
