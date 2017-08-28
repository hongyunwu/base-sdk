package com.why.base.cache;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by wuhongyun on 17-8-28.
 *
 * 使用方法，在application中onCreate方法：调用CrashHandler.init(this);
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler(){

    }

    static class SingleHolder{
        private static final CrashHandler mCrashHandler = new CrashHandler();
    }

    static CrashHandler getInstance(){
        return SingleHolder.mCrashHandler;
    }


    public static void init(Context context){

        getInstance().onInit(context);
    }

    /**
     * 初始化操作
     * @param context
     */
    private void onInit(Context context) {

        this.mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /**
     * 未捕捉的异常
     *
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {

        if (!handleException(e) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Log.e(TAG, "error : ", ex);
            }
            //退出程序
            AppCache.exitApp();
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 保存用户日志到本地
     * @param ex
     */
    private void saveCrashInfo2File(Throwable ex) {
        //TODO 此处当异常产生时，进行log保存

    }

    /**
     * 搜集用户设备信息
     * @param context
     */
    private void collectDeviceInfo(Context context) {
        //TODO 当应用crash时搜集出错设备信息
    }

}
