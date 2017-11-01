package cn.f_ms.runtimepermission.simple.rxjava1;

import android.app.Activity;

import cn.f_ms.runtimepermission.simple.PermissionListener;
import cn.f_ms.runtimepermission.simple.PermissionRefuseResultHelper;
import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermissionHelper;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class RxSimpleRuntimePermissionTransform<T> implements Observable.Transformer<T, T> {

    private final Activity mActivity;
    private final ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener;
    private final String[] permissions;

    public RxSimpleRuntimePermissionTransform(Activity activity, String... permissions) { this(activity, null, permissions); }
    public RxSimpleRuntimePermissionTransform(Activity activity, ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions) {
        this.mActivity = activity;
        this.showRequestPermissionRationaleListener = showRequestPermissionRationaleListener;
        this.permissions = permissions;
    }

    @Override
    public Observable<T> call(Observable<T> upstream) {
        return upstream.flatMap(new Func1<T, Observable<T>>() {
            @Override
            public Observable<T> call(final T t) {
                return Observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(final Subscriber<? super T> subscriber) {
                        SimpleRuntimePermissionHelper.with(mActivity)
                                .permission(permissions)
                                .showPermissionRationaleListener(showRequestPermissionRationaleListener)
                                .execute(new PermissionListener() {
                                    @Override
                                    public void onAllPermissionGranted() {
                                        subscriber.onNext(t);
                                        subscriber.onCompleted();
                                    }

                                    @Override
                                    public void onPermissionRefuse(PermissionRefuseResultHelper resultHelper) {
                                        subscriber.onError(
                                                new PermissionException(resultHelper)
                                        );
                                    }
                                });
                    }
                });
            }
        });
    }
}