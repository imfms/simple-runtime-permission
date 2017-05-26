package cn.f_ms.runtimepermission.simple.rxjava1;

import cn.f_ms.runtimepermission.simple.Permission;
import cn.f_ms.runtimepermission.simple.PermissionRefuseResultHelper;
import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermission;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermissionHelper;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class RxSimpleRuntimePermissionTransform<T> implements Observable.Transformer<T, T> {

    private final SimpleRuntimePermission simpleRuntimePermission;
    private final ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener;
    private final String[] permissions;

    public RxSimpleRuntimePermissionTransform(SimpleRuntimePermission simpleRuntimePermission, String... permissions) {
        this(simpleRuntimePermission, null, permissions);
    }

    public RxSimpleRuntimePermissionTransform(SimpleRuntimePermission simpleRuntimePermission, ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions) {
        this.simpleRuntimePermission = simpleRuntimePermission;
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
                        SimpleRuntimePermissionHelper.with(simpleRuntimePermission)
                                .permission(permissions)
                                .showPermissionRationaleListener(showRequestPermissionRationaleListener)
                                .resultListener(new SimpleRuntimePermission.PermissionListener() {
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
                                })
                                .execute();
                    }
                });
            }
        });
    }
}