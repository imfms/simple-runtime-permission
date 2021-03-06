package cn.f_ms.runtimepermission.simple;

import android.app.Activity;

/**
 * SimpleRuntimePermissionHelper
 *
 * @author _Ms
 * @time 2017/4/26
 */

public class SimpleRuntimePermissionHelper {

    public static class PermissionRequest {

        private SimpleRuntimePermission mSimpleRuntimePermission;
        private String[] mPermissions;
        private ShowRequestPermissionRationaleListener mShowRequestPermissionRationaleListener;

        PermissionRequest(SimpleRuntimePermission simpleRuntimePermission) {
            if (simpleRuntimePermission == null) {
                throw new IllegalArgumentException("simpleRuntimePermission can't be null");
            }

            mSimpleRuntimePermission = simpleRuntimePermission;
        }

        public PermissionRequest permission(String... permissions) {
            this.mPermissions = permissions;
            return this;
        }

        public PermissionRequest showPermissionRationaleListener(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener) {
            this.mShowRequestPermissionRationaleListener = showRequestPermissionRationaleListener;
            return this;
        }

        public void execute(PermissionListener resultListener) {

            if (resultListener == null) {
                throw new IllegalArgumentException("resultListener can't be null");
            }

            if (mPermissions == null
                    || mPermissions.length == 0) {
                throw new IllegalArgumentException("Permission can't be empty/null, please set permission");
            }

            mSimpleRuntimePermission.request(
                    resultListener,
                    this.mShowRequestPermissionRationaleListener,
                    this.mPermissions
            );
        }
    }

    public static PermissionRequest with(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("activity can't be null");
        }

        return new PermissionRequest(new SimpleRuntimePermission(activity));
    }
}
