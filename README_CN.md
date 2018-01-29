[![](https://jitpack.io/v/imfms/simple-runtime-permission.svg)](https://jitpack.io/#imfms/simple-runtime-permission)


# simple-runtime-permission

使用简单的Android动态权限处理器

本项目参考了以下仓库，在此向原作者致以崇高敬意
> 核心实现思想学习及参考了[tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)

- [tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)
- [hotchemi/PermissionsDispatcher](https://github.com/hotchemi/PermissionsDispatcher)
- [yanzhenjie/AndPermission](https://github.com/yanzhenjie/AndPermission)

## 使用环境
Android API_LEVEL >= 11

## 引用方式

- Gradle 当前最新版本为 [![](https://jitpack.io/v/imfms/simple-runtime-permission.svg)](https://jitpack.io/#imfms/simple-runtime-permission)


```groovy
repositories {
  maven { url 'https://jitpack.io' } // If not already there
}

dependencies {

  /*
  BaseLibrary
  */
  compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission:${最新版本}'

  /*
  Support For RxJava1
  */
  compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission-rxjava1:${最新版本}'

  /*
  Support For RxJava2
  */
  compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission-rxjava2:${最新版本}'
}
```

## 使用方法

### Base Library
通过回调方式对请求权限进行响应

#### 使用示例

```java
// 请求读取通讯录联系人、拨打电话权限，当用户曾经拒绝过权限获取(未勾选不再提示)时提示用户为什么我们需要这个权限
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
#### 参数解释

```java
// 创建实例, 链式调用 
SimpleRuntimePermissionHelper.with(mActivity)
  // 权限字符串可变数组，接受可变String参数或String数组，可从 Manifest.permission.* 引用获取权限字符串
  .permission(PermissionStr...)
  // [可选] 显示请求权限理由提示回调监听器，可参考官方文档: https://developer.android.com/training/permissions/requesting.html#explain
  .showPermissionRationaleListener(new ShowRequestPermissionRationaleListener() {
      @Override
      public void onShowRequestPermissionRationale(ShowRequestPermissionRationaleControler controler, String[] permissions) {
        /* 当需要展示请求权限解释提示时被回调时，可在此自定义提示ui，在用户选择同意或拒绝操作后调用控制器的相关方法
        controler.doContinue(); // 用户同意
        controler.doCancel(); // 用户拒绝
        */
      }

      @Override
      public void onRequestPermissionRationaleRefuse(String[] permissions) {
          /*
          当请求权限解释提示被用户拒绝
          ShowRequestPermissionRationaleControler.doCancel() 被调用时
          */
      }
  })
  // 开始请求
  .execute(new PermissionListener() {
      @Override
      public void onAllPermissionGranted() {
          // 当所有指定请求权限被用户同意
      }

      @Override
      public void onPermissionRefuse(PermissionRefuseResultHelper resultHelper) {
          // 当有任何一个权限被拒绝
      }
  });
```

### For RxJava1

1. 实例化 RxSimpleRuntimePermission

   ```java
   RxSimpleRuntimePermission(Activity activity)
   ```

2. 调用请求权限方法
    - compose 使用Rxjava compose操作符对请求权限行为进行合并，当遇到错误则封装到PermissionException并抛出到订阅者onError

        ```java
        <T> RxSimpleRuntimePermissionTransform<T> compose(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions)
        ```

    - request 生成被观察者，开发者直接订阅结果，当遇到错误则封装到PermissionException并抛出到订阅者onError

        ```java
        Observable<None> request(ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener, String... permissions)
        ```

    - Permisssion Exception

        ```java
        public class PermissionException extends RuntimeException {
        	public final PermissionRefuseResultHelper result;
        }
        ```

3. 使用示例

    ```java
    // 请求读取通讯录联系人、拨打电话权限，当用户曾经拒绝过权限获取(未勾选不再提示)时提示用户为什么我们需要这个权限
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

> 同 For RxJava1

## 相关链接
- Android官方运行时权限文档 [使用系统权限](https://developer.android.com/training/permissions/index.html)
