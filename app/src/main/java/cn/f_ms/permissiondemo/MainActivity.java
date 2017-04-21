package cn.f_ms.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.f_ms.rx_easy_runtime_permission.EasyRxRuntimePermission;
import cn.f_ms.rx_easy_runtime_permission.RuntimePermissionException;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        rxPermissions = new RxPermissions(mActivity);

        requestPermission();
    }

    private void requestPermission() {


        EasyRxRuntimePermission.request(rxPermissions, mActivity, new DialogRequestSubscribe(mActivity), Manifest.permission.READ_CONTACTS)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Toast.makeText(mActivity, "GetedPermission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof RuntimePermissionException) {
                            String result = ((RuntimePermissionException) e).type().toString();

                            Toast.makeText(mActivity, "result:" + result, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class DialogRequestSubscribe implements ObservableOnSubscribe<Boolean> {

        private Activity mActivity;

        public DialogRequestSubscribe(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void subscribe(@NonNull final ObservableEmitter<Boolean> e) throws Exception {
            showTipDialog(new SelectListener() {
                @Override
                public void onSelect(boolean isSelect) {
                    e.onNext(isSelect);
                    e.onComplete();
                }
            });

        }

        interface SelectListener {
            void onSelect(boolean isSelect);
        }

        private void showTipDialog(final SelectListener listener) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("亲")
                    .setMessage("等下给个权限呗~")
                    .setPositiveButton("准", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onSelect(true);
                        }
                    })
                    .setNegativeButton("就不给", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onSelect(false);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

    }
}
