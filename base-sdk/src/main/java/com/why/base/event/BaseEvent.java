package com.why.base.event;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wuhongyun on 17-8-28.
 * 作为eventbus时间的积累
 */

public class BaseEvent<E> {

    public BaseEvent(E mEvent) {
        this(mEvent,true);
    }

    public BaseEvent(E mEvent, boolean mAvailable){
        this.mEvent = mEvent;
        this.mAvailable = mAvailable;
    }
    private static  AtomicInteger mAtomicInteger = new AtomicInteger(0);
    /**
     * 事件的code值，可以用于区分事件
     */
    public int mEventCode = -1;

    /**
     * 事件需要传输的对象
     */
    public E mEvent;

    /**
     * 事件的event是否可用
     */
    public boolean mAvailable;
    /**
     * 获取事件的code
     * @return code
     */
    public int getEventCode(){
        if (mEventCode == -1){
            mEventCode = mAtomicInteger.incrementAndGet();
        }
        return mEventCode;
    }

    /**
     * 设置事件对象
     *
     * @param event 传递的事件
     */
    public void setEvent(E event){
        this.mEvent = event;
    }

    /**
     * 获取事件
     * @return event
     */
    public E getEvent(){

        return mEvent;
    }

}
