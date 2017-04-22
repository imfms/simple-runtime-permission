package cn.f_ms.rx_easy_runtime_permission;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class EasyRuntimePermissionTransformer<T> implements ObservableTransformer<T, Boolean> {


    private static class ShouldRationaleTransformer<T> implements ObservableTransformer<T, PermissionType> {

        private final Activity mActivity;
        private RxPermissions rxPermissions;
        private final String[] mPermission;

        ShouldRationaleTransformer(Activity activity, RxPermissions rxPermissions, String... permission) {
            mActivity = activity;
            this.rxPermissions = rxPermissions;
            this.mPermission = permission;
        }

        @Override
        public ObservableSource<PermissionType> apply(@NonNull Observable<T> upstream) {
            return upstream.map(new Function<T, Boolean>() {
                @Override
                public Boolean apply(@NonNull T t) throws Exception {

                    for (String permission : mPermission) {
                        if (!rxPermissions.isGranted(permission)) {
                            return false;
                        }
                    }
                    return true;
                }
            })
                    .zipWith(
                            rxPermissions.shouldShowRequestPermissionRationale(mActivity, mPermission),
                            new BiFunction<Boolean, Boolean, PermissionType>() {
                                @Override
                                public PermissionType apply(@NonNull Boolean isHavePermission, @NonNull Boolean isShouldShowRationale) throws Exception {

                                    if (isHavePermission) {
                                        return PermissionType.PASS;
                                    }

                                    if (isShouldShowRationale) {
                                        return PermissionType.REQUEST_WITH_TIPS;
                                    } else {
                                        return PermissionType.REQUEST;
                                    }
                                }
                            }
                    );
        }
    }

    /* permission handle type */
    private enum PermissionType {
        PASS, REQUEST_WITH_TIPS, REQUEST
    }

    /* RxPermissions */
    private RxPermissions rxPermissions;

    /* ShouldRequestPermissionTip Observer */
    private ObservableOnSubscribe<Boolean> mRequestPermissTipObserver;

    private String[] mPermission;

    private Activity mActivity;

    public EasyRuntimePermissionTransformer(RxPermissions rxPermissions, String... permission) {
        this(rxPermissions, null, null, permission);
    }

    public EasyRuntimePermissionTransformer(RxPermissions rxPermissions, Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {

        if (rxPermissions == null) {
            throw new NullPointerException();
        }
        if (permission == null) {
            throw new NullPointerException();
        }
        if (permission.length == 0) {
            throw new IllegalArgumentException("request permission can't be empty");
        }

        mRequestPermissTipObserver = shouldShowRationaleListener;
        this.rxPermissions = rxPermissions;
        mPermission = permission;
        mActivity = activity;
    }

    @Override
    public ObservableSource<Boolean> apply(@NonNull Observable<T> upstream) {

        Observable<PermissionType> requestObser;

        if (mActivity == null) {
            requestObser = upstream.map(new Function<T, PermissionType>() {
                @Override
                public PermissionType apply(@NonNull T t) throws Exception {
                    return PermissionType.REQUEST;
                }
            });
        } else {
            requestObser = upstream.compose(
                    new ShouldRationaleTransformer<T>(mActivity, rxPermissions, mPermission)
            );
        }

        return requestObser
                .flatMap(new Function<PermissionType, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(@NonNull PermissionType permissionType) throws Exception {

                        switch (permissionType) {
                            case PASS:
                                return Observable.just(true);

                            case REQUEST_WITH_TIPS:

                                if (mRequestPermissTipObserver == null) {

                                    return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                        @Override
                                        public void subscribe(@NonNull final ObservableEmitter<Boolean> e) throws Exception {
                                            requestPermission(e, mPermission);
                                        }
                                    });
                                } else {

                                    return Observable.create(mRequestPermissTipObserver)
                                            .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {

                                                @Override
                                                public ObservableSource<Boolean> apply(@NonNull final Boolean tipsIsAgree) throws Exception {

                                                    return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                                        @Override
                                                        public void subscribe(@NonNull final ObservableEmitter<Boolean> e) throws Exception {

                                                            if (!tipsIsAgree) {
                                                                e.onError(
                                                                        new RuntimePermissionException(RuntimePermissionException.TYPE.USER_REFUSE_TIPS)
                                                                );
                                                                return;
                                                            }
                                                            requestPermission(e, mPermission);
                                                        }
                                                    });
                                                }
                                            });
                                }

                            case REQUEST:
                            default:
                                return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                    @Override
                                    public void subscribe(@NonNull final ObservableEmitter<Boolean> e) throws Exception {
                                        requestPermission(e, mPermission);
                                    }
                                });
                        }
                    }
                });

    }

    /**
     * request permission & handle result to Observer
     *
     * @param e          Observer
     * @param permission permission
     */
    private void requestPermission(@NonNull final ObservableEmitter<Boolean> e, String... permission) {
        rxPermissions.requestEach(permission)
                .subscribe(new Consumer<Permission>() {

                    @Override
                    public void accept(@NonNull Permission permission) throws Exception {

                        if (permission.granted) {

                            e.onNext(true);
                            e.onComplete();
                        } else if (permission.shouldShowRequestPermissionRationale) {

                            e.onError(
                                    new RuntimePermissionException(RuntimePermissionException.TYPE.USER_REFUSE)
                            );
                        } else {

                            e.onError(
                                    new RuntimePermissionException(RuntimePermissionException.TYPE.REFUSE_NEVER_ASK)
                            );
                        }

                    }
                });
    }
}