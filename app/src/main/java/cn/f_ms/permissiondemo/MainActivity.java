package cn.f_ms.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.f_ms.easy_runtime_permission.EasyRuntimePermission;
import cn.f_ms.easy_runtime_permission.Permission;
import cn.f_ms.easy_runtime_permission.PermissionFragment;
import cn.f_ms.easy_runtime_permission.ShowRequestPermissionRationaleListener;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private PermissionFragment mFragment;
    private EasyRuntimePermission easyPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        easyPermission = new EasyRuntimePermission(mActivity);

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission("Congratulation, You Geted Permission");
            }
        });

    }

    private void requestPermission(String s) {

        easyPermission.request(new EasyRuntimePermission.PermissionListener() {
            @Override
            public void onAllPermissionGranted() {
                Toast.makeText(mActivity, "AllGet!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRefuse(Permission[] allPermissionsResult, Permission[] grantedPermissionResult, Permission[] refusePermissionResult) {

                String str = String.format("all: %s\ngranted: %s\nrefuse:%s",
                        allPermissionsResult, grantedPermissionResult, refusePermissionResult
                );

                Toast.makeText(mActivity, str, Toast.LENGTH_SHORT).show();

            }
        }, new ShowRequestPermissionRationaleListener() {
            @Override
            public void onShowRequestPermissionRationale(final ShowRequestPermissionRationaleControler controler, String[] permissions) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("给个权限呗~")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controler.doContinue();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controler.doCancel();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }

            @Override
            public void onRequestPermissionRationaleRefuse(String[] permissions) {
                Toast.makeText(mActivity, "你居然不同意", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE);

    }

}
