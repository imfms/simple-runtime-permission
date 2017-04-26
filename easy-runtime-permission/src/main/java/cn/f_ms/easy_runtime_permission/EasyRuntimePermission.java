package cn.f_ms.easy_runtime_permission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;

import java.util.ArrayList;

/**
 * EasyRuntimePermission Helper Class
 *
 * @author _Ms
 * @time 2017/4/26
 */
public class EasyRuntimePermission {

    /**
     * Permission Callback Listener
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
         * @param allPermissionsResult       all of permission result
         * @param grantedPermissionResult    all of granted permission result, maybe length == 0
         * @param refusePermissionResult     all of refuse permission result, maybe length == 0
         */
        void onPermissionRefuse(
                Permission[] allPermissionsResult,
                Permission[] grantedPermissionResult,
                Permission[] refusePermissionResult
        );
    }

    public static final String TAG = "tag_easy_permission";

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
     * @param listener                                  permission result callback listener
     * @param permissions                               permissions
     */
    public void request(final PermissionListener listener, final String... permissions) {
        request(listener, null, permissions);
    }

    /**
     * request permission
     * when API_VERSION less API23 return Permission.isGranted = true
     *
     * @param listener                                  permission result callback listener
     * @param showRequestPermissionRationaleListener    if need show request permission rational
     * @param permissions                               permissions
     */
    public void request(final PermissionListener listener, final ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, final String... permissions) {

        if (listener == null) { throw new IllegalArgumentException("Permission Listener can't be null"); }
        if (permissions == null
                || permissions.length == 0) {
            throw new IllegalArgumentException("Permission can't be empty");
        }

        /* if API_VERSION less API23 or all permission were granted return all permission true */
        if (!isM()
                || isAllGranted(permissions)) {
            Permission[] requestResult = new Permission[permissions.length];

            for (int x = 0; x < permissions.length; x++) {
                requestResult[x] = new Permission(permissions[x], true);
            }

            listener.onAllPermissionGranted();
            return;
        }

        /* if need show request permission rationale */
        if (showRequestPermissionRationaleListener != null) {

            ArrayList<String> shouldShowPermissionList = new ArrayList<>(permissions.length);
            for (String permission : permissions) {
                if (isShouldShowRequestPermissionRationale(permission)) {
                    shouldShowPermissionList.add(permission);
                }
            }

            /* need show request permission rationale */
            if (!shouldShowPermissionList.isEmpty()) {

                final String[] shouldShowRequestPermissionArray = shouldShowPermissionList.toArray(new String[shouldShowPermissionList.size()]);

                showRequestPermissionRationaleListener.onShowRequestPermissionRationale(
                        new ShowRequestPermissionRationaleListener.ShowRequestPermissionRationaleControler() {
                    @Override
                    public void doContinue() {
                        requestPermission(permissions, listener);
                    }

                    @Override
                    public void doCancel() {
                        showRequestPermissionRationaleListener.onRequestPermissionRationaleRefuse(shouldShowRequestPermissionArray);
                    }

                }, shouldShowRequestPermissionArray);

            }
            /* don't need show request permission rationale */
            else {
                requestPermission(permissions, listener);
            }
        }
        /* don't need show request permission rationale */
        else {
            requestPermission(permissions, listener);
        }
    }

    private void requestPermission(String[] permissions, final PermissionListener listener) {

        mPermissionFragment.request(new PermissionFragment.OnRequestPermissionsResult() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] permissionResults) {

                if (isAllGranted(permissionResults)) {

                    // callback
                    listener.onAllPermissionGranted();
                }
                else {

                    ArrayList<Permission> allPermissionList = new ArrayList<>(permissions.length);
                    ArrayList<Permission> grantedPermissionList = new ArrayList<>(permissions.length / 2);
                    ArrayList<Permission> refusedPermissionList = new ArrayList<>(permissions.length / 2);

                    /* ignore granted permission */
                    String[] grantedPermission = getAllGrantedPermission(permissions);
                    for (String permission : grantedPermission) {
                        allPermissionList.add(new Permission(permission, true));
                    }

                    for (int x = 0; x < permissions.length; x++) {

                        String permission = permissions[x];
                        int permissionResult = permissionResults[x];

                        /* local granted permisssion */
                        if (permissionResult == PackageManager.PERMISSION_GRANTED) {

                            Permission p = new Permission(permission, true);
                            allPermissionList.add(p);
                            grantedPermissionList.add(p);
                        }
                        /* local refused permisssion */
                        else {

                            Permission p = new Permission(permission, false, isShouldShowRequestPermissionRationale(permission));
                            allPermissionList.add(p);
                            refusedPermissionList.add(p);
                        }
                    }

                    // callback
                    listener.onPermissionRefuse(
                            allPermissionList.toArray(
                                    new Permission[allPermissionList.size()]
                            ),
                            grantedPermissionList.toArray(
                                    new Permission[grantedPermissionList.size()]
                            ),
                            refusedPermissionList.toArray(
                                    new Permission[refusedPermissionList.size()]
                            )
                    );
                }

            }
        }, getAllUnGrantedPermission(permissions));
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

    private boolean isAllGranted(int[] permissionResults) {

        for (int result : permissionResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private String[] getAllGrantedPermission(String[] permissions) {
        ArrayList<String> unGrantedPermissions = new ArrayList<>(permissions.length);

        for (String permission : permissions) {
            if (isGranted(permission)) {
                unGrantedPermissions.add(permission);
            }
        }

        return unGrantedPermissions.toArray(new String[unGrantedPermissions.size()]);
    }

    private String[] getAllUnGrantedPermission(String[] permissions) {

        ArrayList<String> unGrantedPermissions = new ArrayList<>(permissions.length);

        for (String permission : permissions) {
            if (!isGranted(permission)) {
                unGrantedPermissions.add(permission);
            }
        }

        return unGrantedPermissions.toArray(new String[unGrantedPermissions.size()]);
    }
}
