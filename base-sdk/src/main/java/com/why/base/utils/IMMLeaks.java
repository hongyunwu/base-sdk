package com.why.base.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 输入法内存泄露
 */
public class IMMLeaks {

  public static class ReferenceCleaner
      implements MessageQueue.IdleHandler, View.OnAttachStateChangeListener,
      ViewTreeObserver.OnGlobalFocusChangeListener {

    private final InputMethodManager inputMethodManager;
    private final Field mHField;
    private final Field mServedViewField;
    private final Method finishInputLockedMethod;

    public ReferenceCleaner(InputMethodManager inputMethodManager, Field mHField, Field mServedViewField,
        Method finishInputLockedMethod) {
      this.inputMethodManager = inputMethodManager;
      this.mHField = mHField;
      this.mServedViewField = mServedViewField;
      this.finishInputLockedMethod = finishInputLockedMethod;
    }

    @Override public void onGlobalFocusChanged(View oldFocus, View newFocus) {
      if (newFocus == null) {
        return;
      }
      if (oldFocus != null) {
        oldFocus.removeOnAttachStateChangeListener(this);
      }
      Looper.myQueue().removeIdleHandler(this);
      newFocus.addOnAttachStateChangeListener(this);
    }

    @Override public void onViewAttachedToWindow(View v) {
    }

    @Override public void onViewDetachedFromWindow(View v) {
      v.removeOnAttachStateChangeListener(this);
      Looper.myQueue().removeIdleHandler(this);
      Looper.myQueue().addIdleHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override public boolean queueIdle() {
      clearInputMethodManagerLeak();
      return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void clearInputMethodManagerLeak() {
      try {
        Object lock = mHField.get(inputMethodManager);
        // This is highly dependent on the InputMethodManager implementation.
        synchronized (lock) {
          View servedView = (View) mServedViewField.get(inputMethodManager);
          if (servedView != null) {

            boolean servedViewAttached = servedView.getWindowVisibility() != View.GONE;

            if (servedViewAttached) {
              // The view held by the IMM was replaced without a global focus change. Let's make
              // sure we get notified when that view detaches.

              // Avoid double registration.
              servedView.removeOnAttachStateChangeListener(this);
              servedView.addOnAttachStateChangeListener(this);
            } else {
              // servedView is not attached. InputMethodManager is being stupid!
              Activity activity = extractActivity(servedView.getContext());
              if (activity == null || activity.getWindow() == null) {
                // Unlikely case. Let's finish the input anyways.
                finishInputLockedMethod.invoke(inputMethodManager);
              } else {
                View decorView = activity.getWindow().peekDecorView();
                boolean windowAttached = decorView.getWindowVisibility() != View.GONE;
                if (!windowAttached) {
                  finishInputLockedMethod.invoke(inputMethodManager);
                } else {
                  decorView.requestFocusFromTouch();
                }
              }
            }
          }
        }
      } catch (IllegalAccessException | InvocationTargetException unexpected) {
        Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
      }
    }

    private Activity extractActivity(Context context) {
      while (true) {
        if (context instanceof Application) {
          return null;
        } else if (context instanceof Activity) {
          return (Activity) context;
        } else if (context instanceof ContextWrapper) {
          Context baseContext = ((ContextWrapper) context).getBaseContext();
          // Prevent Stack Overflow.
          if (baseContext == context) {
            return null;
          }
          context = baseContext;
        } else {
          return null;
        }
      }
    }
  }

}