package com.why.base.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.why.base.enums.BaseUI;
import com.why.base.event.BaseEvent;
import com.why.base.permission.PermissionReq;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

/**
 * Created by wuhongyun on 17-7-17.
 */
@BaseUI
public abstract class BaseActivity<T extends BaseHolder> extends AppCompatActivity implements Handler.Callback {


    private static final boolean DEBUG_MODE = true;
    /**
     * 用于处理handler msg
     */
    protected Handler mHandler = new Handler(Looper.getMainLooper(),this);

    protected T viewHolder;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //处理窗口的全屏，标题栏，状态栏等属性
        if (DEBUG_MODE){
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()//将违规操作打印到日志中
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()//触发违规条件直接crash当前程序
                    .build());
        }
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, getLayoutID(), null);
        setContentView(view);
        EventBus.getDefault().register(this);
        viewHolder = generateViewHolder(view);
        /*if (savedInstanceState!=null)
            dealBundle(savedInstanceState);*/
        dealIntent();
        initData();


    }

    /**
     * 处理横竖屏切换
     * @param savedInstanceState
     */
    protected void dealBundle(Bundle savedInstanceState) {

    }

    /**
     * 处理横竖屏切换
     * @param outState
     */
    protected void saveBundle(Bundle outState) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        dealIntent();
    }

    /**
     * 负责处理新增的intent
     */
    protected  void dealIntent(){};

    /**
     * 通过反射调用构造方法来创建ViewHolder对象
     * @param view
     * @return
     */
    private T generateViewHolder(View view){
        T t = null;
        Class clazz = generateT();
        try {
             t = (T) clazz.getConstructors()[0].newInstance(view);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 当前线程的event回调事件
     * @param event 事件需要继承BaseEvent类型
     * @param <E>
     */
    @Subscribe
    public <E extends BaseEvent>void onEventCallBack(E event){

    }

    /**
     * 获取子类中viewHolder的实际类型
     * @return
     */
    private Class generateT(){
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();//BaseDao<Category>
        return (Class) pt.getActualTypeArguments()[0];
    }

    /**
     * 获取layout的id
     * @return
     */
    public abstract @LayoutRes int getLayoutID();

    /**
     * 获取viewHolder
     * @return T extends BaseHolder，此方法暂时废弃，现已使用反射获取
     */
    public abstract T getViewHolder(View contentView);

    /**
     * 一些全局变量可以存放在此,暂时废弃
     * @return 全局变量holder extends BaseValue
     */
    //public abstract V getContentValues();
    /**
     * 初始化数据，可以做一些网络请求等
     */
    public abstract void initData();
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dealBundle(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveBundle(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewHolder!=null){
            viewHolder.unBind();
        }
        Log.i("MainActivity","ondestroy...");
        EventBus.getDefault().unregister(this);
        //移除所有handler回调
        mHandler.removeCallbacksAndMessages(null);
     
    }

    /**
     * 增加权限申请回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    /**
     * 调起activity页面
     * @param clazz
     * @param finishSelf
     */
    public void gotoSubActivity(Class<? extends BaseActivity> clazz,boolean finishSelf){
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (finishSelf){
            finish();
        }
    }

    public void gotoSubActivity(Class<? extends BaseActivity> clazz,Bundle bundle,boolean finishSelf){

        Intent intent = new Intent(this, clazz);
        if (bundle!=null)intent.putExtras(bundle);
        startActivity(intent);
        if (finishSelf){
            finish();
        }

    }

    public void gotoSubActivity(Class<? extends BaseActivity> clazz,Bundle bundle,int flags,boolean finishSelf){
        Intent intent = new Intent(this, clazz);
        if (bundle!=null)intent.putExtras(bundle);
        intent.addFlags(flags);
        startActivity(intent);
        if (finishSelf){
            finish();
        }

    }

    /**
     *
     * @param msg
     * @return
     * @see  mHandler
     */
    @Override
    public boolean handleMessage(Message msg) {


        return true;
    }
}
