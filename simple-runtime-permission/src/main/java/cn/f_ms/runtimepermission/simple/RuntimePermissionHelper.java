package cn.f_ms.runtimepermission.simple;

import static android.R.id.empty;

/**
 * SimpleRuntimePermissionHelper
 *
 * @author _Ms
 * @time 2017/4/26
 */

public class RuntimePermissionHelper {

    public static class PermissionRequest {

        interface OnExecuteListener {
            void onExecute(PermissionRequest permissionRequest);
        }

        private OnExecuteListener mOnExecuteListener;
        private String[] permissions;
        private SimpleRuntimePermission.PermissionListener resultListener;
        private ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener;

        PermissionRequest(OnExecuteListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("OnExecuteListener can't be empty");
            }
            mOnExecuteListener = listener;
        }

        public PermissionRequest permission(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public PermissionRequest resultListener(SimpleRuntimePermission.PermissionListener permissionListener) {
            resultListener = permissionListener;
            return this;
        }

        public PermissionRequest showPermissionRationaleListener(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener) {
            this.showRequestPermissionRationaleListener = showRequestPermissionRationaleListener;
            return this;
        }

        String[] permissions() { return permissions; }

        SimpleRuntimePermission.PermissionListener resultListener() { return resultListener; }

        ShowRequestPermissionRationaleListener showPermissionRationaleListener() { return showRequestPermissionRationaleListener; }

        public void execute() {
            mOnExecuteListener.onExecute(this);
        }
    }

    public static PermissionRequest with(final SimpleRuntimePermission simpleRuntimePermission) {
        if (simpleRuntimePermission == null) {
            throw new IllegalArgumentException("SimpleRuntimePermission argement can't be empty");
        }

        return new PermissionRequest(new PermissionRequest.OnExecuteListener() {
            @Override
            public void onExecute(PermissionRequest request) {

                simpleRuntimePermission.request(
                        request.resultListener(),
                        request.showPermissionRationaleListener(),
                        request.permissions()
                );
            }
        });
    }
}
