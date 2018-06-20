package com.why.base.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by hongyun.wu@wm-holding.com.cn
 * on 2018/6/20.
 * 用于动态通过广播来控制日志显示
 */

public class LogConfigurationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String filter = intent.getStringExtra("filter");
		if (!TextUtils.isEmpty(filter)&&filter.contains(":")&&filter.split(":").length>=2){

			String[] split = filter.split(":");
			String filterKey = split[0];
			String filterValue = split[1];
			LogConfiguration.getInstance().addFilter(filterKey,filterValue);
		}
		String unFilter = intent.getStringExtra("unFilter");
		if (!TextUtils.isEmpty(unFilter)&&unFilter.contains(":")&&unFilter.split(":").length>=2){

			String[] split = unFilter.split(":");
			String filterKey = split[0];
			String filterValue = split[1];
			LogConfiguration.getInstance().deleteFilter(filterKey,filterValue);
		}

	}
}
