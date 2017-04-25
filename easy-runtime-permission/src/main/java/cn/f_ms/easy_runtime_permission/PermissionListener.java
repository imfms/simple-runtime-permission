package cn.f_ms.easy_runtime_permission;

/**
 * Permission Callback Listener
 * @author f_ms
 * @time 2017/04/25
 */
public interface PermissionListener {
    void onPermissionRequestResult(Permission[] permissions);
}