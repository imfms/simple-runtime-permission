package cn.f_ms.runtimepermission.simple;

/**
 * SimpleRuntimePermissionHelper
 *
 * @author _Ms
 * @time 2017/4/26
 */

public class SimpleRuntimePermissionHelper {

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

        public PermissionRequest showPermissionRationaleListener(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener) {
            this.showRequestPermissionRationaleListener = showRequestPermissionRationaleListener;
            return this;
        }

        public void execute(SimpleRuntimePermission.PermissionListener resultListener) {
            this.resultListener = resultListener;
            mOnExecuteListener.onExecute(this);
        }

        String[] permissions() { return permissions; }

        SimpleRuntimePermission.PermissionListener resultListener() { return resultListener; }

        ShowRequestPermissionRationaleListener showPermissionRationaleListener() { return showRequestPermissionRationaleListener; }
    }

    public static PermissionRequest with(final SimpleRuntimePermission simpleRuntimePermission) {
        if (simpleRuntimePermission == null) {
            throw new IllegalArgumentException("SimpleRuntimePermission argement can't be null");
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
