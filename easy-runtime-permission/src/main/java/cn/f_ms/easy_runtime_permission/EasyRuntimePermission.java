package cn.f_ms.easy_runtime_permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * EasyRuntimePermission Helper Class
 *
 * @author _Ms
 * @time 2017/4/26
 */
public class EasyRuntimePermission {

    public static final String TAG = EasyRuntimePermission.class.getSimpleName();

    private PermissionFragment mPermissionFragment;
    private Activity mActivity;

    public EasyRuntimePermission(Activity activity) {

        if (activity == null) {
            throw new IllegalArgumentException("activity can't be empty");
        }

        mActivity = activity;

        FragmentManager fragmentManager = mActivity.getFragmentManager();

        Fragment permissionFragment = fragmentManager.findFragmentByTag(TAG);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();

            fragmentManager.beginTransaction()
                    .add(permissionFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }

        mPermissionFragment = (PermissionFragment) permissionFragment;
    }

    /**
     * request permission
     * when API_VERSION less API23 return Permission.isGranted = true
     *
     * @param listener    permission result callback listener
     * @param permissions permissions
     */
    public void request(PermissionListener listener, ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions) {
        if (listener == null) {
            throw new IllegalArgumentException("Permission Listener can't be null");
        }

        if (permissions == null
                || permissions.length == 0) {
            throw new IllegalArgumentException("Permission can't be empty");
        }

        if (!isM()) {
            Permission[] requestResult = new Permission[permissions.length];

            for (int x = 0; x < permissions.length; x++) {
                requestResult[x] = new Permission(permissions[x], true);
            }

            listener.onPermissionRequestResult(requestResult);
            return;
        }

        if (isAllGranted(permissions)) {

        }

        if (showRequestPermissionRationaleListener != null) {
            ArrayList<String> shouldShowRequestPermissions = new ArrayList<>(permissions.length);
            for (String permission : permissions) {

            }
            
        }

        mPermissionFragment.request(listener, permissions);
    }

    /**
     * whether permission is revoked
     * when API_VERSION less API23 return false
     */
    public boolean isRevoked(String permission) {
        return isM() && mPermissionFragment.isRevoked(permission);
    }

    /**
     * whether app have permission
     * when API_VERSION less API23 return true
     */
    public boolean isGranted(String permission) {
        return !isM() || mPermissionFragment.isGranted(permission);
    }

    /**
     * is permission should show request rationale
     * when API_VERSION less API23 return false
     */
    public boolean isShouldShowRequestPermissionRationale(String permission) {
        return isM() && mPermissionFragment.isShouldShowRequestPermissionRationale(permission);
    }

    /**
     * whether android api versioin more than 6.0/M
     */
    public boolean isM() {
        return mPermissionFragment.isM();
    }

    private boolean isAllGranted(String[] permissions) {

        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }

        return true;
    }
}
