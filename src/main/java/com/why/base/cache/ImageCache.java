package com.why.base.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.v4.util.LruCache;

import com.why.base.cache.disklrucache.DiskLruCache;
import com.why.base.utils.AppUtils;
import com.why.base.utils.ImageUtils;
import com.why.base.utils.LogUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by wuhongyun on 17-8-28.
 *
 * 图片缓存管理类，使用LRuCache与DiskLruCache结合
 * TODO 暂时只处理了内存缓存的情况
 */

public class ImageCache {

    private LruCache<Integer,Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;
    private Context mContext;

    private ImageCache(){

    }
    private static class SingleHolder{
        private static ImageCache mImageCache = new ImageCache();
    }

    private static ImageCache getInstance(){
        return SingleHolder.mImageCache;
    }

    public static void init(Context context){
       getInstance().onInit(context);

    }

    private void onInit(Context context) {
        //内存缓存
        long maxMemory = Runtime.getRuntime().maxMemory();
        long cacheMemory = maxMemory /8;
        mMemoryCache = new LruCache<Integer, Bitmap>((int) cacheMemory){
            //bitmap的size，字节数
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getRowBytes()*bitmap.getHeight();
            }
            //当bitmap发生移除时调用
            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);

                LogUtils.i("entryRemoved->key:"+key);
            }
        };
        //磁盘缓存
        try {
            File cacheDir = getDiskCacheDir(context, "thumb");

            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            mDiskCache = DiskLruCache.open(cacheDir, AppUtils.getVersionCode(context),1,40*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mContext = context;
    }

    /**
     * 获取缓存路径
     * @param context
     * @param uniqueName
     * @return
     */
    private File getDiskCacheDir(Context context, String uniqueName) {

        String cachePath;
        //如果sdcard可用
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            cachePath = context.getExternalCacheDir().getPath();
        }else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath+File.separator+uniqueName);
    }

    /**
     * 获取缓存的图片
     * @param key
     * @return
     */
    public static Bitmap getCache(@DrawableRes int key){
        return getInstance().get(key);
    }
    private Bitmap get(@DrawableRes int  key){

        Bitmap bitmap = mMemoryCache.get(key);

        if (bitmap==null){

            Drawable drawable =
                    mContext.getResources().getDrawable(key);
            bitmap = ImageUtils.drawableToBitmap(drawable);
            mMemoryCache.put(key,bitmap);
        }

        return bitmap;
    }

    /**
     * 把需要重复使用的图片放在缓存中
     * @param key
     * @param bitmap
     */
    public static void putCache(@DrawableRes int key,Bitmap bitmap){

        getInstance().put(key,bitmap);
    }

    private void put(@DrawableRes int key, Bitmap bitmap) {

        mMemoryCache.put(key,bitmap);
    }

}
