# Android SDK 接入

本文档为**Android**接入 [引力引擎](https://gravity-engine.com/)的技术接入方案，具体 Demo 请参考[GitHub](https://github.com/GravityInfinite/Turbo-Android-Demo)。

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

> [!Tip]
> LATEST_VERSION 请参见[发版记录页](turbo-integrated/android/changelog.md)

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

#### 2.4 上报付费事件

当用户发生付费行为时，需要调用 `trackPayEvent` 方法记录用户付费事件，此事件非常重要，会影响买量和 ROI 统计，请务必重点测试

```java
    /**
 * 上报付费事件
 * @param payAmount     付费金额 单位为分
 * @param payType       付费类型 按照国际标准组织ISO 4217中规范的3位字母，例如CNY人民币、USD美金等
 * @param orderId       订单号
 * @param payReason     付费原因 例如：购买钻石、办理月卡
 * @param payMethod     付费方式 例如：支付宝、微信、银联等
 * @param isFirstPay    是否首次付费
 */
Turbo.get().trackPayEvent(300, "CNY", "order_id" + System.currentTimeMillis(), "月卡", "支付宝", true);
```

#### 2.5 查询用户信息

可以通过 `registerEvent` 方法查询当前用户的买量参数信息

```java
/**
 * 查询用户信息，包括
 * 1. client_id       用户ID
 * 2. channel         用户渠道
 * 3. click_company   用户买量来源，枚举值 为：tencent、bytedance、kuaishou  为空则为自然量用户
 * 4. aid             广告计划ID
 * 5. cid             广告创意ID
 * 6. advertiser_id   广告账户ID
 * 返回示例如下，具体可以打印返回的data查看
 * "user_list": [
 * {
     * "create_time": "2022-09-09 14:50:04",
     * "client_id": "Bn2RhTcU",
     * "advertiser_id": "12948974294275",
     * "channel": "wechat_mini_game",
     * "click_company": "gdt",
     * "aid": "65802182823",
     * "cid": "65580218538",
 * },
 * ]
 */
Turbo.get().queryUserInfoAsync(new QueryUserInfoCallback() {
    @Override
    public void onFailed(String errorMsg) {
        Log.e(TAG, "queryUserInfo onFailed " + errorMsg);
    }

    @Override
    public void onSuccess(UserGetResponseBody.UserGetInfo userGetResponseBody) {
        Log.d(TAG, "queryUserInfo onSuccess " + userGetResponseBody.toString());
    }
});
```

#### 2.6 用户注册事件上报

当用户注册成功时，需要调用 `registerEvent` 方法记录用户注册事件

```java
Turbo.get().trackAppRegisterEvent();
```

#### 2.7 用户登录事件上报

当用户登录成功时，需要调用 `loginEvent` 方法记录用户登录事件

```javascript
Turbo.get().trackAppLoginEvent();
```

#### 2.8 用户注销登录事件上报

当用户注销登录时，需要调用 `logoutEvent` 方法记录用户登出事件

```java
Turbo.get().trackAppLogoutEvent();
```

#### 2.9 设置事件公共属性

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

> [!WARNING]
> 公共属性需要先在`引力引擎后台-->管理中心-->元数据-->事件属性`中添加，否则会上报失败!

#### 2.10 代码埋点追踪自定义事件

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

### 4. 广告相关事件收集

```java
/**
 * 上报广告事件 参数如下
 * @param adUnionType   广告聚合平台类型  （取值为：topon、gromore、admore、self，分别对应Topon、Gromore、Admore、自建聚合）
 * @param adPlacementId 广告瀑布流ID
 * @param adSourceId    广告源ID
 * @param adType        广告类型 （取值为：reward、banner、 native 、interstitial、 splash ，分别对应激励视频广告、横幅广告、信息流广告、插屏广告、开屏广告）
 * @param adnType       广告平台类型（取值为：csj、gdt、ks、 mint 、baidu，分别对应为穿山甲、优量汇、快手联盟、Mintegral、百度联盟）
 * @param ecpm          预估ECPM价格（单位为元）
 * @param duration      广告播放时长（单位为秒）
 * @param isPlayOver    广告是否播放完毕
 */
// 上报广告加载事件
Turbo.get().trackAdLoadEvent("topon", "placement_id", "ad_source_id", "reward", "csj");
// 上报广告展示事件
Turbo.get().trackAdShowEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
// 上报广告点击事件
Turbo.get().trackAdClickEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
// 上报广告开始播放事件
Turbo.get().trackAdPlayStartEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
// 上报广告播放完成事件
Turbo.get().trackAdPlayEndEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1, 50, false);
```
