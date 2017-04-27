package cn.f_ms.runtimepermission.simple.rxjava1;

import java.util.Arrays;

import cn.f_ms.runtimepermission.simple.Permission;

/**
 * PermissionException
 * @author _Ms
 * @time 2017/4/27
 */
public class PermissionException extends RuntimeException {

    public final Permission[] allPermissionsResult;
    public final Permission[] grantedPermissionResult;
    public final Permission[] refusePermissionResult;

    public PermissionException(Permission[] allPermissionsResult, Permission[] grantedPermissionResult, Permission[] refusePermissionResult) {
        super(Arrays.toString(refusePermissionResult));
        this.allPermissionsResult = allPermissionsResult;
        this.grantedPermissionResult = grantedPermissionResult;
        this.refusePermissionResult = refusePermissionResult;
    }
}
