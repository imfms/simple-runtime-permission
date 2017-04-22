package cn.f_ms.rx_easy_runtime_permission;

import android.app.Activity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * RuntimePermission Request RxJava's ObservableTransformer
 *
 * @param <T> Observer<T>
 */
public class EasyRxPermissionTransformer<T> implements ObservableTransformer<T, T> {

    private final Activity mActivity;
    private final ObservableOnSubscribe<Boolean> mShouldShowRationaleListener;
    private final String[] mPermissions;

    /**
     * Reqeust Runtime Permission With ShouldShowRationale
     * @param activity                       Activity
     * @param permission                     permisson[s]
     */
    public EasyRxPermissionTransformer(Activity activity, String... permission) {
        this(activity, null, permission);
    }

    /**
     * Reqeust Runtime Permission With ShouldShowRationale
     * @param activity                       Activity
     * @param shouldShowRationaleListener    CustomShouldShowRationalObserver, emit true is agree, false is refuse
     * @param permission                     permisson[s]
     */
    public EasyRxPermissionTransformer(Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        mActivity = activity;
        this.mShouldShowRationaleListener = shouldShowRationaleListener;
        this.mPermissions = permission;
    }

    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream.flatMap(new Function<T, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply(@NonNull final T t) throws Exception {

                return Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(@NonNull final ObservableEmitter<T> emit) throws Exception {

                        Observable.just(true)
                                .compose(
                                        new PermissionRequestTransformer<>(mActivity, mShouldShowRationaleListener, mPermissions)
                                )
                                .subscribe(new Observer<Boolean>() {

                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        emit.onNext(t);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        emit.onError(e);
                                    }

                                    @Override
                                    public void onComplete() {
                                        emit.onComplete();
                                    }
                                });

                    }
                });
            }
        });
    }
}