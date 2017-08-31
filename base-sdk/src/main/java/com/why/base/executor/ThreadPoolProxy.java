package com.why.base.executor;

import com.why.base.utils.LogUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuhongyun on 17-8-30.
 * 线程代理类
 */

public class ThreadPoolProxy {
    private  int CORE_POOL_SIZE = 0;
    private  int MAXI_MUM_POOL_SIZE = 0;
    private  long KEEP_ALIVE_TIME = 0;
    private LinkedBlockingQueue<Runnable> mBlockingQueue;
    private ThreadPoolExecutor mPool;

    /**
     * 构造方法
     * @param CORE_POOL_SIZE 线程常驻量
     * @param MAXI_MUM_POOL_SIZE 线程最大量
     * @param KEEP_ALIVE_TIME 线程空闲保活时间
     */
    public ThreadPoolProxy(int CORE_POOL_SIZE, int MAXI_MUM_POOL_SIZE, long KEEP_ALIVE_TIME) {
        this.CORE_POOL_SIZE = CORE_POOL_SIZE;
        this.MAXI_MUM_POOL_SIZE = MAXI_MUM_POOL_SIZE;
        this.KEEP_ALIVE_TIME = KEEP_ALIVE_TIME;
        //线程缓存大小
        mBlockingQueue = new LinkedBlockingQueue<>(48);
    }
    //执行取消暂停继续

    /**
     * 默认构造，线程池大小以<CpuInfo>为准
     */
    public ThreadPoolProxy() {
        this.CORE_POOL_SIZE = Math.max(2, Math.min(CpuInfo.getCpuNumber() - 1, 4));
        this.MAXI_MUM_POOL_SIZE = CpuInfo.getCpuNumber() * 2 + 1;
        this.KEEP_ALIVE_TIME = 30;
        mBlockingQueue = new LinkedBlockingQueue<>(48);
    }

    /**
     * 执行任务
     * @param runnable
     */
    public synchronized void excute(Runnable runnable){
        LogUtils.i("excute...");
        if (runnable==null){
            return;
        }
        getPool().execute(runnable);
    }

    /**
     * 暂停任务
     * @param runnable
     */
    public synchronized void pause(Runnable runnable){
        LogUtils.i("pause...");
        if (runnable==null){
            return;
        }
        if (contain(runnable)){
            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogUtils.i("pause...");
        }
    }

    /**
     * 继续执行任务
     * @param runnable
     */
    public synchronized void resume(Runnable runnable){
        LogUtils.i("resume...");
        if (runnable==null){
            return;
        }
        if (contain(runnable)){
             runnable.notify();
            LogUtils.i("resume...");
        }
    }

    /**
     * 取消任务
     * @param runnable
     */
    public synchronized void cancel(Runnable runnable){
        LogUtils.i("cancel...");
        if (runnable==null){
            return;
        }
        if (mPool != null && (!mPool.isShutdown()) || mPool.isTerminating()) {
            mPool.getQueue().remove(runnable);
        }
    }

    /**
     * 包含任务
     * @param runnable
     * @return boolean
     */
    public synchronized boolean contain(Runnable runnable){
        LogUtils.i("contain...");
        if (runnable==null){
            return false;
        }
        if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
            return mPool.getQueue().contains(runnable);
        }
        return false;
    }

    /**
     * 平缓的关闭线程池,不在接收新的任务，待老任务执行玩之后就关闭
     */
    public synchronized void shutDown(){

        if (mPool!=null&&(!mPool.isShutdown()||mPool.isTerminating())){
            mPool.shutdown();
            LogUtils.i("shutDown...");
        }

    }

    /**
     * 立即关闭线程池，可能会打断一些正在执行的线程
     */
    public synchronized void shutDownNow(){
        LogUtils.i("mPool:"+mPool/*+",mPool.isShutdown:"+mPool.isShutdown()+",mPool.isTerminating:"+mPool.isTerminating()*/);
        if (mPool!=null&&(!mPool.isShutdown()||mPool.isTerminating())){
            mPool.shutdownNow();
            LogUtils.i("shutDownNow...");
        }
    }

    /**
     * 获取当前线程池代理类的的线程池
     *
     ThreadPoolExecutor.AbortPolicy() - 默认
     抛出java.util.concurrent.RejectedExecutionException异常 终止策略是默认的饱和策略；

     ThreadPoolExecutor.CallerRunsPolicy()
     当抛出RejectedExecutionException异常时，会调rejectedExecution方法 调用者运行策略实现了一种调节机制，该策略既不会抛弃任务也不会爆出异常，而是将任务退回给调用者，从而降低新任务的流量

     ThreadPoolExecutor.DiscardOldestPolicy()
     抛弃旧的任务；当新提交的任务无法保存到队列中等待执行时将抛弃最旧的任务，然后尝试提交新任务。如果等待队列是一个优先级队列，抛弃最旧的策略将导致抛弃优先级最高的任务，因此AbortPolicy最好不要和优先级队列一起使用。

     ThreadPoolExecutor.DiscardPolicy()
     抛弃当前的任务
     * @return
     */
    private synchronized ThreadPoolExecutor getPool(){

        if (mPool==null || mPool.isShutdown()){

            mPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXI_MUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, mBlockingQueue);
        }
        LogUtils.i("getPool:"+mPool);
        return mPool;
    }

}
