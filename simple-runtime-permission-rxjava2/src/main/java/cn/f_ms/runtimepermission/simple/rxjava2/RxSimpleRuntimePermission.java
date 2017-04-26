package cn.f_ms.runtimepermission.simple.rxjava2;

import android.app.Activity;

import cn.f_ms.runtimepermission.simple.SimpleRuntimePermission;

/**
 * RxSimplePermissionHelper class
 * @author _Ms
 * @time 2017/4/26
 */

public class RxSimpleRuntimePermission {

    private SimpleRuntimePermission mSimpleRuntimePermission;

    public RxSimpleRuntimePermission(Activity activity) {
        mSimpleRuntimePermission = new SimpleRuntimePermission(activity);
    }



}
