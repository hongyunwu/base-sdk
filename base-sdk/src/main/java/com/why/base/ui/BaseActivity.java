package com.why.base.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.why.base.enums.BaseUI;
import com.why.base.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

/**
 * Created by wuhongyun on 17-7-17.
 */
@BaseUI
public abstract class BaseActivity<T extends BaseHolder> extends AppCompatActivity {


    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected T viewHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //处理窗口的全屏，标题栏，状态栏等属性

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
    }
}
