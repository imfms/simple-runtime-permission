package cn.f_ms.runtimepermission.simple.rxjava2;

import cn.f_ms.runtimepermission.simple.PermissionRefuseResultHelper;
import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermission;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermissionHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RxSimpleRuntimePermissionTransform<T> implements ObservableTransformer<T, T> {

        private final SimpleRuntimePermission simpleRuntimePermission;
        private final ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener;
        private final String[] permissions;

        public RxSimpleRuntimePermissionTransform(SimpleRuntimePermission simpleRuntimePermission, String... permissions) { this(simpleRuntimePermission, null, permissions); }
        public RxSimpleRuntimePermissionTransform(SimpleRuntimePermission simpleRuntimePermission, ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions) {
            this.simpleRuntimePermission = simpleRuntimePermission;
            this.showRequestPermissionRationaleListener = showRequestPermissionRationaleListener;
            this.permissions = permissions;
        }

        @Override
        public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
            return upstream.flatMap(new Function<T, ObservableSource<T>>() {
                @Override
                public ObservableSource<T> apply(@NonNull final T t) throws Exception {
                    return Observable.create(new ObservableOnSubscribe<T>() {
                        @Override
                        public void subscribe(@NonNull final ObservableEmitter<T> e) throws Exception {
                            SimpleRuntimePermissionHelper.with(simpleRuntimePermission)
                                    .permission(permissions)
                                    .showPermissionRationaleListener(showRequestPermissionRationaleListener)
                                    .execute(new SimpleRuntimePermission.PermissionListener() {
                                        @Override
                                        public void onAllPermissionGranted() {
                                            e.onNext(t);
                                            e.onComplete();
                                        }

                                        @Override
                                        public void onPermissionRefuse(PermissionRefuseResultHelper resultHelper) {
                                            e.onError(
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