package cn.f_ms.runtimepermission.simple.rxjava1;

import java.util.Arrays;
import java.util.Collections;

import cn.f_ms.runtimepermission.simple.PermissionRefuseResultHelper;

/**
 * PermissionException
 * @author _Ms
 * @time 2017/4/27
 */
public class PermissionException extends RuntimeException {

    public final PermissionRefuseResultHelper result;

    public PermissionException(PermissionRefuseResultHelper resultHelper) {
        super(resultHelper.getRefusePermissions().toString());
        result = resultHelper;
    }

    public PermissionRefuseResultHelper getRefuseResult() {return result;}
}
