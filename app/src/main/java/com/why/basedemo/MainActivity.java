package com.why.basedemo;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.why.base.utils.LogUtils;

import java.io.FileDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private Runnable runnable;
    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runnable = new Runnable() {
            @Override
            public void run() {
                while (true&&!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //break;
                    }
                    LogUtils.i("ThreadName->" + Thread.currentThread().getName());

                }
            }
        };

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ImemoryFile imemoryFile = ImemoryFile.Stub.asInterface(service);

                try {
                    FileDescriptor fileDescriptor = imemoryFile.getFileDescriptor().getFileDescriptor();

                    Method getInt$ = FileDescriptor.class.getMethod("getInt$");
                    int value = (int) getInt$.invoke(fileDescriptor);
                    LogUtils.i("value:"+value);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this,MemoryFileService.class),conn, Service.BIND_AUTO_CREATE);
        //ThreadManager.getInstance().getDefaultPool().excute(runnable);
        //ThreadManager.getShortPool().execute(thread);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //ThreadManager.getInstance().getDefaultPool().pause(runnable);
    }

    @Override
    protected  void onResume() {
        super.onResume();

        //ThreadManager.getInstance().getDefaultPool().resume(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ThreadManager.getInstance().getDefaultPool().cancel(runnable);
        ThreadManager.getShortPool().shutdown();
        unbindService(conn);
    }
}
