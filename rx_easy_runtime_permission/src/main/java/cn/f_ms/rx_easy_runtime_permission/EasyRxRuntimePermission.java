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

    public static Observable<Boolean> request(Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return request(new RxPermissions(activity), activity, shouldShowRationaleListener, permission);
    }

    public static Observable<Boolean> request(RxPermissions rxPermissions, Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return Observable.just(true)
                .compose(new EasyRuntimePermissionTransformer<>(rxPermissions, activity, shouldShowRationaleListener, permission));
    }

    public static Observable<Boolean> request(RxPermissions rxPermissions, String... permission) {
        return request(rxPermissions, null, null, permission);
    }

    public static Observable<Boolean> request(Activity activity, String... permission) {
        return request(new RxPermissions(activity), permission);
    }

    public ObservableTransformer ensure(Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return ensure(new RxPermissions(activity), activity, shouldShowRationaleListener, permission);
    }

    public ObservableTransformer ensure(RxPermissions rxPermissions, Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return new EasyRuntimePermissionTransformer<>(rxPermissions, activity, shouldShowRationaleListener, permission);
    }

    public ObservableTransformer ensure(Activity activity, String... permission) {
        return ensure(new RxPermissions(activity), null, null, permission);
    }

    public ObservableTransformer ensure(RxPermissions rxPermissions, String... permission) {
        return ensure(rxPermissions, null, null, permission);
    }

}
