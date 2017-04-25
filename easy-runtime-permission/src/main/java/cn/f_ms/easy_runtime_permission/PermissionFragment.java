package cn.f_ms.easy_runtime_permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.permission;

/**
 * Runtime Permission Request Adapter Fragment
 *
 * @author f-ms
 * @time 2017/4/25
 */

public class PermissionFragment extends Fragment {

    private final ArrayList<PermissionRequestBean> mRequestBeanList;

    /** Permission Request Wrapper */
    public static class PermissionRequestBean {
        public final String[] permissions;
        public final PermissionListener listener;

        public PermissionRequestBean() {
            this(null, null)
        }

        public PermissionRequestBean(String[] permissions, PermissionListener listener) {
            this.permissions = permissions;
            this.listener = listener;
        }

        public int hashCode() {
            int result = Arrays.hashCode(permissions);
            result = 31 * result + (listener != null ? listener.hashCode() : 0);
            return result;
        }
    }

    public static final int REQUEST_CODE_RUNTIME_PERMISSION = Integer.MAX_VALUE;

    public PermissionFragment() {
        mRequestBeanList = new ArrayList<>(2);
    }

    /**
     * request permission
     * @param listener       permission result callback listener
     * @param permissions    permissions
     */
    public void request(PermissionListener listener, String... permissions) {

        if (listener == null) {
            throw new IllegalArgumentException("Permission Listener can't be null");
        }

        if (permissions == null
                || permissions.length == 0) {
            throw new IllegalArgumentException("Permission can't be empty");
        }



    }

    /**
     * whether permission is revoked
     * when API_VERSION less API23 return false
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isRevoked(String permission) {
        return isM() && getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    /**
     * whether app have permission
     * when API_VERSION less API23 return true
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isGranted(String permission) {
        return !isM() || getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**  whether android api versioin more than 6.0/M */
    public boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * is permission should show request rationale
     * when API_VERSION less API23 return false
     * */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isShouldShowRequestPermissionRationale(String permission) {
        return isM() && getActivity().shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
