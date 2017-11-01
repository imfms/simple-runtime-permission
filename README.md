[![](https://jitpack.io/v/imfms/simple-runtime-permission.svg)](https://jitpack.io/#imfms/simple-runtime-permission)

# simple-runtime-permission

A simple Android runtime permission handler

[中文文档](README_CN.md)

`Joke with my poor English`

This library refers from those library, give original author high respect.
> The core to achieve the idea of learning and reference [tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)

- [tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)
- [hotchemi/PermissionsDispatcher](https://github.com/hotchemi/PermissionsDispatcher)
- [yanzhenjie/AndPermission](https://github.com/yanzhenjie/AndPermission)

## Environment
Android API_LEVEL >= 11

## Reference Method

- Gradle LatestVersion is [![](https://jitpack.io/v/imfms/simple-runtime-permission.svg)](https://jitpack.io/#imfms/simple-runtime-permission)


        repositories {
            maven { url 'https://jitpack.io' } // If not already there
        }
        
        dependencies {
    
            /*
            BaseLibrary
            */
            compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission:${latest.version}'
            
            /*
            Support For RxJava1
            */
            compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission-rxjava1:${latest.version}'
            
            /*
            Support For RxJava2
            */
            compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission-rxjava2:${latest.version}'
        }

## How to use


0. Declaration Permission in Manifest

> Permission Result Wrapper

```java
class Permission {
    String name; // Permission String
    boolean isGranted; // isGranted
    boolean isShouldShowRequestPermissionRationale; // isShouldShowRequestPermissionRationale (valid when isGranted is false)
}
```

### Base Library
callback mode response request

1. Instance Class SimpleRuntimePermission

        SimpleRuntimePermission(Activity activity)

2. Invoke SimpleRuntimePermission.request to request permission

        void request(
            PermissionListener listener,
            ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener,
            String... permissions
        )

    - String... Permission string array, accept mulit args, recommend select from Manifest.permission.*
    - PermissionListener Permission request callback listener

            // When all of request permissions were granted
            void onAllPermissionGranted()

            // When any permissions were refused
            void onPermissionRefuse(PermissionRefuseResultHelper resultHelper)

    - [Optional] ShowRequestPermissionRationaleListener ShowRequestPermissionRationale callback listener, can refer official document [Requesting Permissions at Run Time#Explain why the app needs permissions](https://developer.android.com/training/permissions/requesting.html#explain)

         // When need show request permission rationale tip
         void onShowRequestPermissionRationale(
             ShowRequestPermissionRationaleControler controler,
             String[] permissions
         )
         ​        
         /*
         When request permission rationale refuse
         when ShowRequestPermissionRationaleControler.doCancel() invoke
         */
         void onRequestPermissionRationaleRefuse(
             String[] permissions
         )
         ​    

    - String[] Need show request permission rationale permission string array
    - ShowRequestPermissionRationaleControler Controler
        - void doContinue() // Do request permission from system (user select agree)
        - void doCancel() // Cancel request action (user select refuse)

4. Sample

    > Request 'read contacts' & 'call phone' permission, When user has refused this permission(not select never tip)  tell user why need this permission

    ```java
    SimpleRuntimePermissionHelper.with(mActivity)
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
        .execute(new PermissionListener() {
            @Override
            public void onAllPermissionGranted() {
                Toast.makeText(mActivity, requestSuccessStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRefuse(PermissionRefuseResultHelper resultHelper) {
                String str = String.format("all: %s\ngranted: %s\nrefuse:%s\nnever_ask: %s",
                        resultHelper.getAllPermissions(),
                        resultHelper.getGrantPermissions(),
                        resultHelper.getRefusePermissions(),
                        resultHelper.getNeverAskAgainPermissions()
                );

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(str)
                        .show();
            }
        });
    ```

### For RxJava1

1.  Instance RxSimpleRuntimePermission

        RxSimpleRuntimePermission(Activity activity)

2. Invoke request permision method
    - compose Use RxJava operator 'compose' to compose request permission action, when get refuse result it will wrapper result to PermissionException for subscriber's onError()

            <T> RxSimpleRuntimePermissionTransform<T> compose(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions)

    - request Create observer, developer can subscribe result, when get refuse result it will wrapper result to PermissionException for subscriber's onError()

            Observable<None> request(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions)

    - Permisssion Exception
    ```java
    public class PermissionException extends RuntimeException {
        public final PermissionRefuseResultHelper result;
    }
    ```

3. Sample

    > Request 'read contacts' & 'call phone' permission, When user has refused this permission(not select never tip)  tell user why need this permission

    ```java
    RxSimpleRuntimePermission rxSimpleRuntimePermission = new RxSimpleRuntimePermission(mActivity);

    Observable.just(requestSuccessStr)
            .compose(rxSimpleRuntimePermission.<String>compose(
                    new ShowRequestPermissionRationaleListener() {
                        @Override
                        public void onShowRequestPermissionRationale(ShowRequestPermissionRationaleControler controler, String[] permissions) {

                            new AlertDialog.Builder(mActivity)
                                    .setTitle("PermissionRequestTip")
                                    .setMessage("I need those permissions, please select agree")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            controler.doContinue();
                                        }
                                    })
                                    .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            controler.doCancel();
                                        }
                                    })
                                    .create()
                                    .show();

                        }

                        @Override
                        public void onRequestPermissionRationaleRefuse(String[] permissions) {
                           Toast.makeText(mActivity, "Fail, user refused request permission tip", Toast.LENGTH_SHORT).show();
                        }
                    }
                    , Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)
            )
            .subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    if (e instanceof cn.f_ms.runtimepermission.simple.rxjava1.PermissionException) {
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNext(String s) {
                    Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
                }
            });
    ```


### For RxJava2

> Same with For RxJava1

## Links
- Android Runtime Permission Official Document [Working with System Permissions](https://developer.android.com/training/permissions/index.html)
