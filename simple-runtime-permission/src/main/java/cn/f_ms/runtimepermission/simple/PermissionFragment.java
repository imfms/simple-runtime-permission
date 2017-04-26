package cn.f_ms.runtimepermission.simple;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Runtime Permission Request Adapter Fragment
 *
 * @author f-ms
 * @time 2017/4/25
 */

public class PermissionFragment extends Fragment {

    /**
     * Permission Callback Listener
     *
     * @author f_ms
     * @time 2017/04/25
     */
    interface OnRequestPermissionsResult {
        void onRequestPermissionsResult(String[] permissions, int[] grantResults);
    }

    /**
     * Permission Request Wrapper
     */
    private static class PermissionRequestBean {

        final String[] permissions;
        final OnRequestPermissionsResult listener;

        PermissionRequestBean() {
            this(null, null);
        }

        PermissionRequestBean(String[] permissions, OnRequestPermissionsResult listener) {
            this.permissions = permissions;
            this.listener = listener;
        }

        public int hashCode() {
            int result = Arrays.hashCode(permissions);
            result = 31 * result + (listener != null ? listener.hashCode() : 0);

            if (result < 0) {
                result = -result;
            }

            return result;
        }
    }

    /**  save request list*/
    private ArrayList<PermissionRequestBean> mRequestBeanList;

    public PermissionFragment() {}

    /**
     * request permission
     * when API_VERSION less API23 return Permission.isGranted = true
     *
     * @param listener    permission result callback listener
     * @param permissions permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    void request(OnRequestPermissionsResult listener, String... permissions) {

        if (mRequestBeanList == null) {
            mRequestBeanList = new ArrayList<>(2);
        }

        PermissionRequestBean permissionRequestBean = new PermissionRequestBean(permissions, listener);
        mRequestBeanList.add(permissionRequestBean);

        requestPermissions(permissions, permissionRequestBean.hashCode());
    }

    /**
     * whether permission is revoked
     * when API_VERSION less API23 return false
     */
    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    /**
     * whether app have permission
     * when API_VERSION less API23 return true
     */
    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PERMISSION_GRANTED;
    }

    /**
     * whether android api versioin more than 6.0/M
     */
    boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * is permission should show request rationale
     * when API_VERSION less API23 return false
     */
    @TargetApi(Build.VERSION_CODES.M)
    boolean isShouldShowRequestPermissionRationale(String permission) {
        return getActivity().shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionRequestBean requestBean = isContainPermissionRequest(requestCode, mRequestBeanList);
        if (requestBean == null) {
            return;
        }

        requestBean.listener.onRequestPermissionsResult(permissions, grantResults);

        removeFromList(requestBean);
    }

    private <T> T isContainPermissionRequest(int requestCode, List<T> list) {

        if (list == null
                || list.isEmpty()) {
            return null;
        }

        for (T t : list) {
            if (requestCode == t.hashCode()) {
                return t;
            }
        }

        return null;
    }

    private void removeFromList(PermissionRequestBean requestBean) {
        mRequestBeanList.remove(requestBean);
        if (mRequestBeanList.isEmpty()) {
            mRequestBeanList = null;
        }
    }
}
