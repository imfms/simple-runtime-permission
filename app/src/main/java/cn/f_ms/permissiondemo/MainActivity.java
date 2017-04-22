package cn.f_ms.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cn.f_ms.rx_easy_runtime_permission.EasyRxPermissionTransformer;
import cn.f_ms.rx_easy_runtime_permission.PermissionException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        requestPermission("Congratulation, You Geted Permission");
    }

    private void requestPermission(String s) {


        Observable.just(s)
                .compose(new EasyRxPermissionTransformer<String>(mActivity, new DialogRequestSubscribe(mActivity), Manifest.permission.READ_CONTACTS))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof PermissionException) {

                            PermissionException.TYPE refuseType = ((PermissionException) e).type();

                            String result = refuseType.toString();

                            switch (refuseType) {
                                case REFUSE_NEVER_ASK:
                                    break;
                                case USER_REFUSE:
                                    break;
                                case USER_REFUSE_TIPS:
                                    break;
                            }

                            Toast.makeText(mActivity, "REFUSE:" + result, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {}
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
                    .setTitle("Tips")
                    .setMessage("Please Give The Permission, I Need It")
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onSelect(true);
                        }
                    })
                    .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
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
