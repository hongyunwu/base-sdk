package com.why.base.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.why.base.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;

/**
 * Created by lenovo on 2017/9/13.
 */

public abstract class BaseDialogFragment<T extends BaseHolder> extends DialogFragment implements Handler.Callback {
	protected Handler mHandler = new Handler(Looper.getMainLooper(),this);

	private View contentView;
	protected T viewHolder;
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL,getStyle());
	}

	public abstract @StyleRes int getStyle();

	/**
	 * 用于构造Dialog
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//保证多次执行onCreateView时布局view引用都相同
		if (contentView==null){
			contentView = inflater.inflate(getLayoutID(),null);
			try {
				viewHolder = generateViewHolder(contentView);
			}catch (Exception e){
				e.printStackTrace();
			}
			initData();

		}
		EventBus.getDefault().register(this);
		Log.i("BaseFragment",getClass().getSimpleName()+" onCreateView()...");
		return contentView;
	}

	protected abstract void initData();

	protected abstract @LayoutRes int getLayoutID();

	/**
	 * 获取子类中viewHolder的实际类型
	 * @return
	 */
	private Class generateT(){
		ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();//BaseDao<Category>
		return (Class)( pt.getActualTypeArguments()[0]);
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

	/**
	 * 负责处理mHandler发送的消息处理
	 * @param msg
	 * @return
	 */
	@Override
	public boolean handleMessage(Message msg) {


		return true;
	}
}
