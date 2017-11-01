package cn.f_ms.runtimepermission.simple;

/**
 * Permission Callback Listener
 *
 * @author f_ms
 * @time 2017/04/26
 */
public interface PermissionListener {

    /**
     * when request permission all granted
     */
    void onAllPermissionGranted();

    /**
     * when some/all permission were refused
     *
     * @param resultHelper request result helper
     */
    void onPermissionRefuse(PermissionRefuseResultHelper resultHelper);
}