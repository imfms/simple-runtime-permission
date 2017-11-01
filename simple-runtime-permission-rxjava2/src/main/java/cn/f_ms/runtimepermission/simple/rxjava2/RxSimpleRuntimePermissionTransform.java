package cn.f_ms.runtimepermission.simple.rxjava2;

import android.app.Activity;

import cn.f_ms.runtimepermission.simple.PermissionListener;
import cn.f_ms.runtimepermission.simple.PermissionRefuseResultHelper;
import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermissionHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RxSimpleRuntimePermissionTransform<T> implements ObservableTransformer<T, T> {

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
        public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
            return upstream.flatMap(new Function<T, ObservableSource<T>>() {
                @Override
                public ObservableSource<T> apply(@NonNull final T t) throws Exception {
                    return Observable.create(new ObservableOnSubscribe<T>() {
                        @Override
                        public void subscribe(@NonNull final ObservableEmitter<T> e) throws Exception {
                            SimpleRuntimePermissionHelper.with(mActivity)
                                    .permission(permissions)
                                    .showPermissionRationaleListener(showRequestPermissionRationaleListener)
                                    .execute(new PermissionListener() {
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