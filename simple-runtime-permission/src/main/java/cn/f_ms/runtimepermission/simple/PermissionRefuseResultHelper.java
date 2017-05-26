package cn.f_ms.runtimepermission.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission Request Refuse Result Wrapper Helper
 *
 * @author _Ms
 * @time 2017/5/26
 */
public final class PermissionRefuseResultHelper {

    private final List<Permission> mPermissions;

    public PermissionRefuseResultHelper(List<Permission> permissions) {
        if (permissions == null) {
            throw new NullPointerException();
        }

        mPermissions = permissions;
    }

    /**
     * get all permission result
     */
    public List<Permission> getAllPermissions() {
        return new ArrayList<>(mPermissions);
    }

    /**
     * get all of grant permission result
     */
    public List<Permission> getGrantPermissions() {

        ArrayList<Permission> grantPermissions = new ArrayList<>();

        for (Permission permission : mPermissions) {
            if (permission.isGranted) {
                grantPermissions.add(permission);
            }
        }
        return grantPermissions;
    }

    /**
     * get all of refuse permission result
     */
    public List<Permission> getRefusePermissions() {
        ArrayList<Permission> refusePermissions = new ArrayList<>();

        for (Permission permission : mPermissions) {
            if (!permission.isGranted) {
                refusePermissions.add(permission);
            }
        }
        return refusePermissions;
    }

    /**
     * get all of never ask again permission result
     */
    public List<Permission> getNeverAskAgainPermissions() {
        ArrayList<Permission> neverAskAgainPerimssions = new ArrayList<>();

        for (Permission permission : mPermissions) {
            if (!permission.isGranted
                    && !permission.isShouldShowRequestPermissionRationale) {
                neverAskAgainPerimssions.add(permission);
            }
        }
        return neverAskAgainPerimssions;
    }

    /**
     * get all of should show request permission rationale permissions
     */
    public List<Permission> getShouldShowRequestPermissionRationalePermissions() {

        ArrayList<Permission> shouldShowRequestPermissionRationalePermissions = new ArrayList<>();

        for (Permission permission : mPermissions) {
            if (!permission.isGranted
                    && permission.isShouldShowRequestPermissionRationale) {
                shouldShowRequestPermissionRationalePermissions.add(permission);
            }
        }
        return shouldShowRequestPermissionRationalePermissions;
    }

    private boolean isExistRefust() { return !isAllGrant(); }

    public boolean isExistShouldShowRequestPermissionRationale() {
        for (Permission permission : mPermissions) {
            if (!permission.isGranted
                    && permission.isShouldShowRequestPermissionRationale) {
                return true;
            }
        }
        return false;
    }

    public boolean isExistNeverAskAgain() {
        for (Permission permission : mPermissions) {
            if (!permission.isGranted
                    && !permission.isShouldShowRequestPermissionRationale) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllGrant() {
        for (Permission permission : mPermissions) {
            if (!permission.isGranted) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllRefuse() {
        for (Permission permission : mPermissions) {
            if (permission.isGranted) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllShouldRequestPermissionRationale() {
        for (Permission permission : mPermissions) {

            if (permission.isGranted) {
                return false;
            }

            if (!permission.isShouldShowRequestPermissionRationale) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllNeverAskAgain() {
        for (Permission permission : mPermissions) {

            if (permission.isGranted) {
                return false;
            }

            if (permission.isShouldShowRequestPermissionRationale) {
                return false;
            }
        }
        return true;
    }
}
