package com.why.base.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.why.base.R;
import com.why.base.event.BaseEvent;
import com.why.base.permission.PermissionReq;
import com.why.base.utils.PreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;

/**
 * Created by lenovo on 2017/9/15.
 *
 * Preference的file_name同PreferenceUtils类中的FILE_NAME一致
 *
 * 在顶部填充一个toolbar
 */

public abstract class BaseSettingsActivity<T extends BaseHolder> extends AppCompatActivity implements Handler.Callback {

	protected Handler mHandler = new Handler(Looper.getMainLooper(), this);
	protected T viewHolder;
	private SettingsPreferenceFragment preferenceFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		//处理窗口的全屏，标题栏，状态栏等属性
		super.onCreate(savedInstanceState);
		LinearLayout contentView = new LinearLayout(this);
		contentView.setOrientation(LinearLayout.VERTICAL);
		setContentView(contentView);
		ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
		layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
		layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
		contentView.setLayoutParams(layoutParams);
		if (getToolBarId() != 0) {
			//顶部toolbar
			Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(getToolBarId(), contentView, false);
			contentView.addView(toolbar, 0);
			//
			toolbar.setId(R.id.tool_bar);
		}

		FrameLayout container = new FrameLayout(this);
		contentView.addView(container);
		ViewGroup.LayoutParams containerLayoutParams = container.getLayoutParams();
		containerLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
		containerLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
		container.setLayoutParams(containerLayoutParams);
		container.setId(R.id.container);


		preferenceFragment = new SettingsPreferenceFragment();
		getFragmentManager()
				.beginTransaction()
				.add(R.id.container, preferenceFragment)
				.commit();

		EventBus.getDefault().register(this);
		viewHolder = generateViewHolder(contentView);
		/*if (savedInstanceState!=null)
            dealBundle(savedInstanceState);*/
		dealIntent();

	}

	protected abstract int getToolBarId();

	/**
	 * 处理横竖屏切换
	 *
	 * @param savedInstanceState
	 */
	protected void dealBundle(Bundle savedInstanceState) {

	}

	/**
	 * 处理横竖屏切换
	 *
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
	protected void dealIntent() {
	}

	/**
	 * 通过反射调用构造方法来创建ViewHolder对象
	 *
	 * @param view
	 * @return
	 */
	private T generateViewHolder(View view) {
		T t = null;

		try {
			Class clazz = generateT();
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
	 *
	 * @param event 事件需要继承BaseEvent类型
	 * @param <E>
	 */
	@Subscribe
	public <E extends BaseEvent> void onEventCallBack(E event) {

	}

	/**
	 * 获取子类中viewHolder的实际类型
	 *
	 * @return
	 */
	private Class generateT() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();//BaseDao<Category>
		return (Class) pt.getActualTypeArguments()[0];
	}


	/**
	 * 获取viewHolder
	 * @return T extends BaseHolder，此方法暂时废弃，现已使用反射获取
	 */
	//public abstract T getViewHolder(View contentView);

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
		if (viewHolder != null) {
			viewHolder.unBind();
		}
		Log.i("MainActivity", "ondestroy...");
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 增加权限申请回调
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);

	}

	/**
	 * 调起activity页面
	 *
	 * @param clazz
	 * @param finishSelf
	 */
	public void gotoSubActivity(Class<? extends BaseActivity> clazz, boolean finishSelf) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		if (finishSelf) {
			finish();
		}
	}

	public void gotoSubActivity(Class<? extends BaseActivity> clazz, Bundle bundle, boolean finishSelf) {

		Intent intent = new Intent(this, clazz);
		if (bundle != null) intent.putExtras(bundle);
		startActivity(intent);
		if (finishSelf) {
			finish();
		}

	}

	public void gotoSubActivity(Class<? extends BaseActivity> clazz, Bundle bundle, int flags, boolean finishSelf) {
		Intent intent = new Intent(this, clazz);
		if (bundle != null) intent.putExtras(bundle);
		intent.addFlags(flags);
		startActivity(intent);
		if (finishSelf) {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int menuId = getMenuId();
		if (menuId != 0) {
			getMenuInflater().inflate(menuId, menu);
			return true;
		}

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 当布局有menu时需重写此方法
	 *
	 * @return
	 */
	public int getMenuId() {
		return 0;
	}

	@Override
	public boolean handleMessage(Message msg) {

		return true;
	}

	public static class SettingsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(getXmlId());
			getPreferenceManager().setSharedPreferencesName(PreferenceUtils.FILE_NAME);
			getPreferenceScreen().setOnPreferenceChangeListener(this);

			((BaseSettingsActivity) getActivity()).initData();
		}

		/**
		 * 去除preference的默认padding
		 * @param inflater
		 * @param container
		 * @param savedInstanceState
		 * @return
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
			for (int i=0;i<view.getChildCount();i++)
			{
				View view1 = view.getChildAt(i);
				view1.setPadding(0,0,0,0);

			}
			return view;
		}

		public
		@XmlRes
		int getXmlId() {

			return ((BaseSettingsActivity) getActivity()).getXmlId();
		}

		/**
		 * @param preferenceScreen
		 * @param preference
		 * @return true 代表点击事件已成功捕捉，无须执行默认动作或返回上层调用链。 例如， 不跳转至默认Intent。
		 * false 代表执行默认动作并且返回上层调用链。例如，跳转至默认Intent
		 */
		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

			((BaseSettingsActivity) getActivity()).onPreferenceTreeClick(preferenceScreen, preference);

			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {


			return ((BaseSettingsActivity) getActivity()).onPreferenceChange(preference, newValue);
		}
	}


	/**
	 *
	 *
	 * @param preferenceScreen
	 * @param preference
	 */
	public void onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {


	}

	/**
	 * @param preference
	 * @param newValue
	 * @return 如果为true，那么写入新值到preference中
	 */
	public boolean onPreferenceChange(Preference preference, Object newValue) {


		return true;
	}

	/**
	 * @param key
	 * @return
	 */
	public Preference findPreference(CharSequence key) {
		if (preferenceFragment != null) {
			preferenceFragment.findPreference(key);
		}
		return null;
	}

	public PreferenceManager getPreferenceManager() {
		if (preferenceFragment != null) {

			preferenceFragment.getPreferenceManager();
		}
		return null;
	}

	public PreferenceScreen getPreferenceScreen() {
		if (preferenceFragment != null) {
			return preferenceFragment.getPreferenceScreen();
		}
		return null;
	}

	public abstract
	@XmlRes
	int getXmlId();

}
