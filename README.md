# Android SDK 接入

本文档为**Android**接入 [turbo 引力引擎](https://gravity-engine.com/)的技术接入方案，具体 Demo 请参考[GitHub](https://github.com/GravityInfinite/Turbo-Android-Demo)。

### 1. 集成引力引擎 SDK

#### 1.1 在项目 build.gradle 文件中，添加 maven 源

```gradle
maven {
    url 'https://nexus.gravity-engine.com/repository/maven-releases/'
}
maven {
    url 'https://nexus.gravity-engine.com/repository/maven-snapshots/'
}
maven { url "https://jitpack.io" }
```

#### 1.2 在 dependencies 中添加依赖

```gradle
implementation "com.plutus.common:turbo:${LATEST_VERSION}"
```

> 📢 注意：LATEST_VERSION 请参见[发版记录页](turbo-integrated/android/changelog.md)

#### 1.3 添加混淆

```gradle
# Turbo
-keep class com.plutus.common.turbo.beans.** {*;}
```

---

### 2. SDK 基本配置

#### 2.1 配置 SDK 的基础环境

在继承自 `Application` 类（如果没有，则需要自行创建，并添加到 `AndroidManifest.xml` 文件中去）的代码中添加如下代码：

```java
public class App extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 调用onAttachBaseContext
        /**
         *       context attachBaseContext传递过来的Context参数
                 isDebug 当前是否为debug模式，上线之后务必传递为false
                 buildType 当前build type，例如：BuildConfig.BUILD_TYPE，如果想要开启debug模式，这里需要传入debug
                 Flavor 当前build flavor，例如：”vip” 非必填
         */
        boolean isDebug = true; // 调试阶段打开可以输出部分日志信息，线上请不要开启，会影响性能
        String buildType = isDebug ? "debug" : "release";
        GravitySDK.onAttachBaseContext(base, isDebug, buildType, "test");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 调用onCreate
        GravitySDK.onCreate(this);
    }
}
```

#### 2.2 SDK 初始化

```java
    /**
     * 在应用的onCreate方法中调用，此方法会初始化Turbo需要的基础参数
     * @param aesKey        加密
     * @param accessToken   项目通行证
     * @param isAgreePrivacy 是否同意隐私弹窗
     */
    // 请尽可能的早调用，暂时只支持使用默认配置
    TurboConfigOptions configOptions = new TurboConfigOptions();
    // 必须每次启动都调用，建议在用户同意隐私政策弹窗之后，传入isAgreePrivacy参数为true
    Turbo.get().init("k7ZjSgc1Z8j551UJUNLlWA==", "x5emsWAxqnlwqpDH1j4bbicR8igmhruT", true, configOptions);
```

#### 2.3 用户注册

在用户注册或者可以获取到用户唯一性信息时调用此方法，推荐首次安装启动时调用，后续其他接口，均需要等 `register` 接口完成之后才能继续调用

```java
    /**
     * 在用户注册或者可以获取到用户唯一性信息时调用，推荐首次安装启动时调用
    （有自己的用户体系的调用这个）
     * @param clientId  用户唯一id
     * @param userName  用户名
     * @param channel   渠道号
     * @param version   版本号
     * @param callback 注册回调接口
     */
    Turbo.get().register("test_client_id_1", "testUser", "base", 1, new RegisterCallback() {
        @Override
        public void onFailed(String errorMsg) {
            Log.d(TAG, "register failed " + errorMsg);
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "register success");
            Log.d(TAG, " status " + Turbo.get().isRegisterSuccess());
        }
    });

    /**
     * 在用户注册或者可以获取到用户唯一性信息时调用，推荐首次安装启动时调用（
    没有用户体系的版本调用这个）
     * @param channel   渠道号
     * @param version   版本号
     * @param callback 注册回调接口
     */
    Turbo.get().register("base", 1, new RegisterCallback() {
        @Override
        public void onFailed(String errorMsg) {
            Log.d(TAG, "register failed " + errorMsg);
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "register success");
            Log.d(TAG, " status " + Turbo.get().isRegisterSuccess());
        }
    });
```

#### 2.4 上报买量关键行为

当发生以下买量节点事件时，通过 `handleEvent` 方法上报事件

```java
    /**
     * 处理关键行为上报
     * @param eventType 关键行为类型 分为
        激活 activate
        注册 register
        付费 pay
        次留 twice
        关键行为 key_active
     * @see com.plutus.common.turbo.beans.HandleEventType
     */
    Turbo.get().handleEvent(HandleEventType.PAY);
```

#### 2.5 用户注册事件上报

当用户注册成功时，需要调用 `registerEvent` 方法记录用户注册事件

```java
Turbo.get().trackAppRegisterEvent();
```

#### 2.6 用户登录事件上报

当用户登录成功时，需要调用 `loginEvent` 方法记录用户登录事件

```javascript
Turbo.get().trackAppLoginEvent();
```

#### 2.7 用户注销登录事件上报

当用户注销登录时，需要调用 `logoutEvent` 方法记录用户登出事件

```java
Turbo.get().trackAppLogoutEvent();
```

#### 2.8 设置事件公共属性

对于所有事件都需要添加的属性，可在初始化 SDK 前，调用 `registerSuperProperties()` 将属性注册为公共属性：

```java
JSONObject jsonObject = new JSONObject();
try {
    jsonObject.put("role_type", "法师");
    jsonObject.put("role_power", 1000);
} catch (JSONException e) {
    e.printStackTrace();
}
Turbo.get().registerSuperProperties(jsonObject);
```

> 📢 注意：公共属性需要先在`引力引擎后台-->管理中心-->元数据-->事件属性`中添加，否则会上报失败。

#### 2.9 代码埋点追踪自定义事件

在文件顶部使用 import 引入 SDK 文件，然后调用 `track()` 方法，可以记录用户自定义事件。

```java
JSONObject jsonObject = new JSONObject();
try {
    jsonObject.put("guild_id", "100");
    jsonObject.put("guild_level", 10);
    jsonObject.put("guild_name", "北方之狼");
    Turbo.get().track("AddGuild", jsonObject);
} catch (JSONException e) {
    e.printStackTrace();
}
```

---

### 3. 用户属性相关调用

#### 3.1 设置用户属性

`profileSet()` 方法可以设置用户属性，同一个 key 被多次设置时，value 的值会进行覆盖替换：

```java
// 若某key已存在则覆盖,否则将自动创建并赋值
JSONObject jsonObject = new JSONObject();
jsonObject.put("$name", "turboUserName");
jsonObject.put("$gender", "男");
Turbo.get().profileSet(jsonObject);
```

#### 3.2 记录初次设定的用户属性

对于只在首次设置时有效的属性，我们可以使用 `profileSetOnce()` 记录这些属性。与 `profileSet()` 方法不同的是，如果被设置的用户属性已存在，则这条记录会被忽略而不会覆盖已有数据，如果属性不存在则会自动创建。因此，`profileSetOnce()` 适用于为用户设置首次激活时间、首次注册时间等属性。例如：

```java
Turbo.get().profileSetOnce("$first_visit_time", TimeUtils.formatTime(System.currentTimeMillis(), TimeUtils.YYYY_MM_DD_HH_MM_SS));
```

#### 3.3 数值类型的属性

对于数值型的用户属性，可以使用 `profileIncrement()` 对属性值进行累加。常用于记录用户付费次数、付费额度、积分等属性。例如：

```java
// 增加或减少一个用户的某个NUMBER类型的Profile值
Turbo.get().profileIncrement("$age", 27);
```

#### 3.4 列表类型的属性

对于用户喜爱的电影、用户点评过的餐厅等属性，可以记录列表型属性，例如：

```java
// 向某个用户的某个数组类型的Profile添加一个或者多个值,默认不去重
Set<String> movies = new HashSet<>();
movies.add("Interstellar");
movies.add("The Negro Motorist Green Book");
Turbo.get().profileAppend("Movies", movies);
```

#### 3.5 用户属性的删除

调用 `profileDelete()` 方法，将把当前用户属性清空

```java
// // 删除一个用户的整个 Profile
Turbo.get().profileDelete();
```

#### 3.6 属性取消

如果需要取消已设置的某个用户属性，可以调用 `profileUnset()` 进行取消：

```java
// 将某个用户的某些属性值设置为空
Turbo.get().profileUnset("$name");
```

#### License

Under BSD license，you can check out the license file
