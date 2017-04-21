package cn.f_ms.rx_easy_runtime_permission;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;

/**
 * EasyRxRuntimePermissionHelper
 */
public final class EasyRxRuntimePermission {

    public static Observable<Boolean> request(RxPermissions rxPermissions, Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return Observable.just(true)
                .compose(new EasyRuntimePermissionTransformer<>(rxPermissions, activity, shouldShowRationaleListener, permission));
    }

    public static Observable<Boolean> request(RxPermissions rxPermissions, Activity activity, String... permission) {
        return request(rxPermissions, activity, null, permission);
    }

    public ObservableTransformer ensure(RxPermissions rxPermissions, Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return new EasyRuntimePermissionTransformer<>(rxPermissions, activity, shouldShowRationaleListener, permission);
    }

    public ObservableTransformer ensure(RxPermissions rxPermissions, Activity activity, String... permission) {
        return new EasyRuntimePermissionTransformer<>(rxPermissions, activity, null, permission);
    }

}
