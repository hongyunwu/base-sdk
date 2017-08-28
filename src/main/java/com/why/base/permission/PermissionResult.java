package com.why.base.permission;

public interface PermissionResult {
    void onGranted();

    void onDenied();
}
