package com.why.base.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;
import com.why.base.cache.AppCache;
import com.why.base.enums.BaseUI;
import com.why.base.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;

/**
 * Created by wuhongyun on 17-7-17.
 */
@BaseUI
public abstract class BaseFragment<T extends BaseHolder> extends Fragment {

    protected T viewHolder;
    private View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //保证多次执行onCreateView时布局view引用都相同
        if (contentView==null){
            contentView = inflater.inflate(getLayoutID(),null);

            viewHolder = generateViewHolder(contentView);

        }
        EventBus.getDefault().register(this);
        Log.i("BaseFragment",getClass().getSimpleName()+" onCreateView()...");
        initData();
        return contentView;
    }

    /**
     * 获取layoutID
     * @return 资源id
     */
    public abstract  @LayoutRes int getLayoutID();

    /**
     * 进行一些初始化操作
     */
    public abstract void initData();


    @Override
    public void onDestroy() {
        super.onDestroy();
        //不要unbindview
        //检测内存泄露问题
        RefWatcher refWatcher = AppCache.getRefWatcher();
        if (refWatcher!=null)
            refWatcher.watch(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
        } catch (Exception e) {
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
}
