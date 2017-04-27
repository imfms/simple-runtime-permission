[![](https://jitpack.io/v/imfms/simple-runtime-permission.svg)](https://jitpack.io/#imfms/simple-runtime-permission)


# simple-runtime-permission

使用简单的Android动态权限处理器

本项目参考了以下仓库，在此其原作者致以崇高敬意
> 核心实现思想学习及参考了[tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)

- [tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)
- [hotchemi/PermissionsDispatcher](https://github.com/hotchemi/PermissionsDispatcher)
- [yanzhenjie/AndPermission](https://github.com/yanzhenjie/AndPermission)

## 使用环境
Android API_LEVEL >= 11

## 引用方式

- Gradle 当前最新版本为 [![](https://jitpack.io/v/imfms/simple-runtime-permission.svg)](https://jitpack.io/#imfms/simple-runtime-permission)


        repositories {
            maven { url 'https://jitpack.io' } // If not already there
        }
        
        dependencies {
    
            /*
            BaseLibrary
            */
            compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission:V${最新版本}'
            
            /*
            Support For RxJava1
            */
            compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission-rxjava1:V${最新版本}'
            
            /*
            Support For RxJava2
            */
            compile 'com.github.imfms.simple-runtime-permission:simple-runtime-permission-rxjava2:V${最新版本}'
        }
        
## 使用方法


0. 在清单文件中声明所需权限

> 权限结果包装

```java
class Permission {
    String name; // 权限字符串
    boolean isGranted; // 是否已同意
    boolean isShouldShowRequestPermissionRationale; // 是否需要提示解释为什么需要权限(当isGranted为false时生效，否则忽略)
}
```

### base_library
通过回调方式对请求权限进行响应

1. 实例化类 SimpleRuntimePermission

        SimpleRuntimePermission(Activity activity)

2. 通过类SimpleRuntimePermission.request方法请求权限

        void request(
            PermissionListener listener,
            ShowRequestPermissionRationaleListener showRequestPermissionRationaleListener,
            String... permissions
        )
        
    - String... 权限字符串可变数组，接受可变String参数或String数组，可从 Manifest.permission.* 引用获取权限字符串
    - PermissionListener 权限请求回调监听器
    
            // 当所有指定请求权限被用户同意
            void onAllPermissionGranted()

            // 当有任何一个权限被拒绝
            void onPermissionRefuse(
                    Permission[] allPermissionsResult, // 请求的所有权限的结果集, 包含以下两种结果集
                    Permission[] grantedPermissionResult, // 请求的所有权限中成功的结果集
                    Permission[] refusePermissionResult // 请求的所有权限中被拒绝的结果集
            )
            
    - [可选]ShowRequestPermissionRationaleListener 显示请求权限理由提示回调监听器，参考官方文档 [运行时请求权限->解释应用为什么需要权限](https://developer.android.com/training/permissions/requesting.html#explain)
        
            // 当需要展示请求权限解释提示
            void onShowRequestPermissionRationale(
                ShowRequestPermissionRationaleControler controler,
                String[] permissions
            )
        
            /*
            当请求权限解释提示被用户拒绝
            ShowRequestPermissionRationaleControler.doCancel()被调用时
            */
            void onRequestPermissionRationaleRefuse(
                String[] permissions
            )
                
    
    - String[] 需要请求权限解释的权限字符串集
    - ShowRequestPermissionRationaleControler 控制器
        - void doContinue() // 向系统请求权限(用户同意)
        - void doCancel() // 取消权限请求动作(当用户拒绝)
        
4. 使用示例

    > 请求读取通讯录联系人、拨打电话权限，当用户曾经拒绝过权限获取(未勾选不再提示)时提示用户为什么我们需要这个权限
    
    ```java
    SimpleRuntimePermission simplePermission = new SimpleRuntimePermission(mActivity);

    simplePermission.request(
            new SimpleRuntimePermission.PermissionListener() {
                @Override
                public void onAllPermissionGranted() {
                    Toast.makeText(mActivity, "Success, geted permission", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRefuse(Permission[] allPermissionsResult, Permission[] grantedPermissionResult, Permission[] refusePermissionResult) {
                    Toast.makeText(mActivity, "Fail, some permission were refused", Toast.LENGTH_SHORT).show();
                }
            }
            , new ShowRequestPermissionRationaleListener() {
                @Override
                public void onShowRequestPermissionRationale(final ShowRequestPermissionRationaleControler controler, String[] permissions) {
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
            , Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS
    );
    ```
    
5. 为方便使用，另提供了一个辅助类'SimpleRuntimePermissionHelper'，链式风格 (参考[yanzhenjie/AndPermission](https://github.com/yanzhenjie/AndPermission))
    ```java
    SimpleRuntimePermissionHelper.with(SimpleRuntimePermission)
            .permission(...)
            .showPermissionRationaleListener(...)
            .resultListener(...)
            .execute();
     ```
     
### support for rxjava1

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
        public final Permission[] allPermissionsResult;
        public final Permission[] grantedPermissionResult;
        public final Permission[] refusePermissionResult;
    }
    ```
    
3. 使用示例

    > 请求读取通讯录联系人、拨打电话权限，当用户曾经拒绝过权限获取(未勾选不再提示)时提示用户为什么我们需要这个权限
    
    ```java
    RxSimpleRuntimePermission rxSimpleRuntimePermission = new RxSimpleRuntimePermission(mActivity);

    Observable.just(requestSuccessStr)
            .compose(rxSimpleRuntimePermission.<String>compose(
                    new ShowRequestPermissionRationaleListener() {
                        @Override
                        public void onShowRequestPermissionRationale(ShowRequestPermissionRationaleControler controler, String[] permissions) {

                        }

                        @Override
                        public void onRequestPermissionRationaleRefuse(String[] permissions) {

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
    

### support for rxjava2        

> 同 support for rxjava1

## 相关链接
- 官方文档 [使用系统权限](https://developer.android.com/training/permissions/index.html)
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
