package cn.f_ms.rx_easy_runtime_permission;

import android.app.Activity;

import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;

/**
 * EasyRxRuntimePermissionHelper
 */
public final class EasyRxPermission {

    /**
     * Reqeust Runtime Permission
     * @param activity                       Activity
     * @param permission                     permisson[s]
     * @return RxJava ObserbleTransformer, use operator 'compose' attach
     */
    public static <T> ObservableTransformer<T, T> compose(Activity activity, String... permission) {
        return compose(activity, null, permission);
    }

    /**
     * Reqeust Runtime Permission With ShouldShowRationale
     * @param activity                       Activity
     * @param shouldShowRationaleListener    CustomShouldShowRationalObserver, emit true is agree, false is refuse
     * @param permission                     permisson[s]
     * @return RxJava ObserbleTransformer, use operator 'compose' attach
     */
    public static <T> ObservableTransformer<T, T> compose(Activity activity, ObservableOnSubscribe<Boolean> shouldShowRationaleListener, String... permission) {
        return new EasyRxPermissionTransformer<>(activity, shouldShowRationaleListener, permission);
    }
}
