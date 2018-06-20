package com.why.base.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by hongyun.wu@wm-holding.com.cn
 * on 2018/6/20.
 * 日志配置类
 */

public class LogConfiguration {
	private ArrayList<String> mFilterPackages = new ArrayList<>();
	private ArrayList<String> mFilterClasses = new ArrayList<>();
	private ArrayList<String> mFilterTags = new ArrayList<>();
	private ArrayList<String> mFilterContains = new ArrayList<>();
	private static LogConfiguration mLogConfiguration = null;
	private LogConfiguration(){

	}
	public static LogConfiguration getInstance() {
		if (mLogConfiguration == null){
			synchronized (LogConfiguration.class){
				if (mLogConfiguration==null){
					mLogConfiguration = new LogConfiguration();
				}
			}
		}
		return mLogConfiguration;
	}

	public static boolean filter(StackTraceElement stackTraceElement) {
		boolean filter = false;
		for (String packageName : getInstance().mFilterPackages){
			if (stackTraceElement.getClassName().startsWith(packageName)){
				filter = true;
				break;
			}
		}
		if (getInstance().mFilterClasses.contains(stackTraceElement.getClassName())){
			filter = true;
		}

		if (getInstance().mFilterTags.contains(LogUtils.getDefaultTag(stackTraceElement))){
			filter = true;
		}

		for (String contain : getInstance().mFilterContains){
			if (!TextUtils.isEmpty(stackTraceElement.getClassName()) && stackTraceElement.getClassName().contains(contain)) {
				filter = true;
				break;
			}
		}

		return filter;
	}

	public void addFilter(String filterKey, String filterValue) {
		switch (filterKey){
			case "package":
				addPackage(filterValue);
				break;
			case "class":
				addClass(filterValue);
				break;
			case "tag":
				addTag(filterValue);
				break;
			case "contain":
				addContain(filterValue);
				break;
		}
	}
	public void deleteFilter(String filterKey, String filterValue) {
		switch (filterKey){
			case "package":
				deletePackage(filterValue);
				break;
			case "class":
				deleteClass(filterValue);
				break;
			case "tag":
				deleteTag(filterValue);
				break;
			case "contain":
				deleteContain(filterValue);
				break;
		}
	}
	private void addContain(String filterValue) {
		if (!mFilterContains.contains(filterValue))
			mFilterContains.add(filterValue);
	}

	private void addTag(String filterValue) {
		if (!mFilterTags.contains(filterValue))
			mFilterTags.add(filterValue);
	}

	private void addClass(String filterValue) {
		if (!mFilterClasses.contains(filterValue))
			mFilterClasses.add(filterValue);
	}

	private void addPackage(String filterValue) {
		if (!mFilterPackages.contains(filterValue))
			mFilterPackages.add(filterValue);
	}

	private void deleteContain(String filterValue) {
		if (mFilterContains.contains(filterValue))
			mFilterContains.remove(filterValue);
	}

	private void deleteTag(String filterValue) {
		if (mFilterTags.contains(filterValue))
			mFilterTags.remove(filterValue);
	}

	private void deleteClass(String filterValue) {
		if (mFilterClasses.contains(filterValue))
			mFilterClasses.remove(filterValue);
	}

	private void deletePackage(String filterValue) {
		if (mFilterPackages.contains(filterValue))
			mFilterPackages.remove(filterValue);
	}



	public static class Builder{

		private final LogConfiguration mLogConfiguration;
		private ArrayList<String> filterPackages = new ArrayList<>();
		private ArrayList<String> filterClasses = new ArrayList<>();
		private ArrayList<String> filterTags = new ArrayList<>();
		private ArrayList<String> filterContains = new ArrayList<>();
		public Builder(){
			mLogConfiguration = LogConfiguration.getInstance();
			//mLogConfiguration.clear();
			filterPackages.clear();
			filterClasses.clear();
			filterTags.clear();
			filterContains.clear();
		}

		public Builder filterPackage(@NonNull String packageName){
			if (!filterPackages.contains(packageName))filterPackages.add(packageName);
			return this;
		}

		public Builder filterClass(@NonNull String className){
			if (!filterClasses.contains(className))filterClasses.add(className);
			return this;
		}
		public Builder filterTag(@NonNull String tagName){
			if (!filterTags.contains(tagName))filterTags.add(tagName);
			return this;
		}

		public Builder filterContains(@NonNull String containName){
			if (!filterContains.contains(containName))filterContains.add(containName);
			return this;
		}
		public void build(){
			for (String packageName :filterPackages){
				mLogConfiguration.addPackage(packageName);
			}
			for (String className :filterClasses){
				mLogConfiguration.addClass(className);
			}
			for (String tagName :filterTags){
				mLogConfiguration.addTag(tagName);
			}
			for (String containName :filterContains){
				mLogConfiguration.addContain(containName);
			}
		}
	}

	public void clear() {
		mFilterPackages.clear();
		mFilterClasses.clear();
		mFilterTags.clear();
		mFilterContains.clear();
	}


}
