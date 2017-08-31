package com.why.basedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.why.base.utils.LogUtils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wuhongyun on 17-8-30.
 */

public class MemoryFileService extends Service {

    private MemoryFile memoryFile;
    private FileDescriptor fileDescriptor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        try {
            memoryFile = new MemoryFile("wwdadas", 3000);
            Method getFileDescriptor = MemoryFile.class.getMethod("getFileDescriptor");
            fileDescriptor = (FileDescriptor) getFileDescriptor.invoke(memoryFile);
            Method getInt$ = FileDescriptor.class.getMethod("getInt$");
            int value = (int) getInt$.invoke(fileDescriptor);
            LogUtils.i("value:"+value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MyBinder();
    }


    class MyBinder extends ImemoryFile.Stub{

        @Override
        public ParcelFileDescriptor getFileDescriptor() throws RemoteException {


            try {
                return ParcelFileDescriptor.class.getConstructor(FileDescriptor.class).newInstance(fileDescriptor);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setValue(int val) throws RemoteException {

        }
    }
}
