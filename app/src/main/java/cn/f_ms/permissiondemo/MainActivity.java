package cn.f_ms.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.f_ms.easy_runtime_permission.Permission;
import cn.f_ms.easy_runtime_permission.PermissionFragment;
import cn.f_ms.easy_runtime_permission.PermissionListener;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private PermissionFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission("Congratulation, You Geted Permission");
            }
        });

    }

    private void requestPermission(String s) {



    }

}
