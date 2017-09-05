package com.why.base.net;

import com.why.base.cache.AppCache;
import com.why.base.utils.LogUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lenovo on 2017/9/3.
 *
 * 用于请求网络
 *
 */

public class BaseImpl<Service extends BaseSerVice> {

	private static Retrofit mRetrofit;
	protected Service mService;

	public BaseImpl(){
		initRetrofit();
		Class<Service> clazz = generateService();
		LogUtils.i("clazz:"+clazz);
		mService = mRetrofit.create(clazz);
	}


	private Class<Service> generateService(){
		return (Class<Service>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];


	}

	/**
	 * 对retrofit进行初始化
	 */
	private void initRetrofit() {
		if (mRetrofit==null){
			synchronized (BaseImpl.class){
				if (mRetrofit==null){
					//进行retrofit初始化
					//日志请求类
					HttpLoggingInterceptor mLogInterceptor = new HttpLoggingInterceptor();
					mLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
					Interceptor mNetworkInterceptor = new Interceptor() {
						@Override
						public Response intercept(Chain chain) throws IOException {
							//为所有的请求都加上apiKey
							Request request = chain.request();
							//TODO

							return chain.proceed(request);
						}
					};
					//HTTP的状态码为401时会调用
					Authenticator mAuthenticator = new Authenticator() {
						@Override
						public Request authenticate(Route route, Response response) throws IOException {
							//TODO 如果此处需要重新授权，那么就在此处刷新token

							return response.request().newBuilder().build();
						}
					};
					OkHttpClient client = new OkHttpClient.Builder()
							.readTimeout(10, TimeUnit.SECONDS)
							.writeTimeout(10,TimeUnit.SECONDS)
							.connectTimeout(10,TimeUnit.SECONDS)
							.retryOnConnectionFailure(true)//是否重试
							.addInterceptor(mLogInterceptor)
							.addNetworkInterceptor(mNetworkInterceptor)//网络拦截器
							.authenticator(mAuthenticator)//是否授权拦截器
							.build();
					mRetrofit = new Retrofit.Builder()
							.baseUrl(AppCache.getBaseUrl())
							.client(client)
							.addConverterFactory(GsonConverterFactory.create())
							.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
							.build();
				}
			}
		}
	}

}
