package cn.f_ms.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.f_ms.runtimepermission.simple.Permission;
import cn.f_ms.runtimepermission.simple.RuntimePermissionHelper;
import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermission;


public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private SimpleRuntimePermission mSimplePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        mSimplePermission = new SimpleRuntimePermission(mActivity);

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

    }

    private void requestPermission() {

        RuntimePermissionHelper.with(mSimplePermission)
                .permission(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)
                .showPermissionRationaleListener(new ShowRequestPermissionRationaleListener() {
                    @Override
                    public void onShowRequestPermissionRationale(final ShowRequestPermissionRationaleControler controler, String[] permissions) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle("Tips")
                                .setMessage("Please Give Me Those Permissions")
                                .setPositiveButton("Yes, I Will", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        controler.doContinue();
                                    }
                                })
                                .setNegativeButton("Sorry, I can't", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(mActivity, "Well, You refused.", Toast.LENGTH_SHORT).show();
                    }
                })
                .resultListener(new SimpleRuntimePermission.PermissionListener() {
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
                })
                .execute();

    }

}
