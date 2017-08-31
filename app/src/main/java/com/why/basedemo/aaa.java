package com.why.basedemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wuhongyun on 17-8-30.
 */

public class aaa implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
