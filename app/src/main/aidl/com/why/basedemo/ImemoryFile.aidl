// ImemoryFile.aidl
package com.why.basedemo;

// Declare any non-default types here with import statements

interface ImemoryFile {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    ParcelFileDescriptor getFileDescriptor();
    void setValue(int val);
}
