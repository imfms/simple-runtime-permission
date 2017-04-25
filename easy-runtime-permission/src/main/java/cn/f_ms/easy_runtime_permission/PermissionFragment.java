package cn.f_ms.easy_runtime_permission;

import android.app.Fragment;
import android.os.Build;

/**
 * Runtime Permission Request Adapter Fragment
 *
 * @author f-ms
 * @time 2017/4/25
 */

public class PermissionFragment extends Fragment {

    public static final int REQUEST_CODE_RUNTIME_PERMISSION = 0;

    public PermissionFragment() {



    }

    public void request(PermissionListener listener, ShowRequestPermissionRationaleListener showRequestListener, String... permissions) {

        if (listener == null) {
            throw new IllegalArgumentException("Permission Listener can't be null");
        }

        if (permissions == null
                || permissions.length == 0) {
            throw new IllegalArgumentException("permission can't be empty");
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isGranted(String permission) {
        getActivity().checkSelfPermission(permission)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
