package cn.f_ms.runtimepermission.simple.rxjava2;

import android.app.Activity;

import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import io.reactivex.Observable;

/**
 * RxSimplePermissionHelper class
 * @author _Ms
 * @time 2017/4/26
 */

public class RxSimpleRuntimePermission {

    private final Activity mActivity;

    public RxSimpleRuntimePermission(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("activity can't be null");
        }
        mActivity = activity;
    }

    public <T> RxSimpleRuntimePermissionTransform<T> compose(String... permissions) { return compose(null, permissions); }
    public <T> RxSimpleRuntimePermissionTransform<T> compose(final ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, final String... permissions) {
        return new RxSimpleRuntimePermissionTransform<>(
                mActivity,
                showRequestPermissionRationaleListener,
                permissions
        );
    }

    public Observable<None> request(String... permissions) { return request(null, permissions); }
    public Observable<None> request(final ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, final String... permissions) {
        return Observable.just(None.NONE)
                .compose(this.<None>compose(showRequestPermissionRationaleListener, permissions));
    }
}
