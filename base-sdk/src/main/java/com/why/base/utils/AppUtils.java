package com.why.base.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import com.why.base.enums.BaseUtil;

/**
 * Created by wuhongyun@autoio.cn on 2017/5/25.
 */
@BaseUtil
public class AppUtils {

	private AppUtils()
	{
        /* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 *
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取当前的版本号，可用于作为版本升级
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context){

		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 判断当前进程是否是主进程 - processName==packageName
	 *
	 * @param context 需要进行判断的上下文环境
	 * @return processName.equals(packageName)
	 */
	public static boolean isUIProcess(Context context){
		int myPid = Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String mProcessName = "";
		for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()){
			if (processInfo.pid ==myPid){

				mProcessName = processInfo.processName;
				break;
			}
		}

		return mProcessName.equals(context.getPackageName());
	}


}
