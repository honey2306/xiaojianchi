# 小坚持 Little Steps

一个温柔手帐风格的 Android 多习惯打卡 App。用尽可能低的成本完成打卡，并保留过去每一次坚持的证据。

## 第一版功能

- 多习惯：名称、图标、颜色、每日提醒，支持编辑与归档
- 今日打卡：一键完成或撤销，累计天数与今日进度即时更新
- 坚持足迹：累计、当前连续、最长连续，按月浏览手绘印记日历
- 开始日期可以设到过去：之前已经坚持的连续天数可以一次记下，不必从第一天开始
- 之后仍是最远 7 天的补记窗口，开始日期之前与未来日期不可打卡，已有记录可撤销
- 每次打卡可写一句感受（最多 80 字）
- 本地温和提醒，可设置时间并跳转到系统通知设置
- 跨天回到 App 时自动刷新今日状态
- 打卡功能完全离线，无需账号；Room 本地数据库保存所有数据
- GitHub 应用内更新：检查版本、下载进度、SHA-256 校验、系统确认安装

首次打开没有示例习惯，可以从自己的第一件小事开始。

## 下载与更新

- 正式版下载页：https://honey2306.github.io/xiaojianchi/
- GitHub Releases：https://github.com/honey2306/xiaojianchi/releases
- 发布流程与更新机制见 [UPDATING.md](UPDATING.md)。

GitHub Actions 会运行测试和 Lint，并使用固定密钥签名 Release APK。每次发布还会生成 `update.json` 和同步下载页。

## 技术

- Kotlin + Jetpack Compose / Material 3
- Room + Kotlin Coroutines / Flow
- AlarmManager + 系统本地通知
- minSdk 26（Android 8.0），target/compile SDK 35

## 构建

需要 JDK 17 和 Android SDK 35。

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export ANDROID_HOME="$HOME/Library/Android/sdk"
./gradlew testDebugUnitTest assembleDebug
```

Debug APK 输出到：

```text
app/build/outputs/apk/debug/app-debug.apk
```

安装到已连接的设备或模拟器：

```bash
"$ANDROID_HOME/platform-tools/adb" install -r app/build/outputs/apk/debug/app-debug.apk
```

## 测试

```bash
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

测试覆盖连续/累计统计、今日记录唯一性、撤销、归档保留历史，以及中文创建习惯 → 打卡 → 写感受 → 查看足迹的完整 UI 流程。

## 隐私

- 不上传习惯、打卡或备注；网络权限只用于从本项目 GitHub Release 检查和下载更新。
- APK 只允许 HTTPS 和本项目 GitHub 下载地址，安装前必须通过 SHA-256 校验和系统签名验证。
- 不允许系统云备份；卸载 App 会清除本机数据，请在卸载前确认。
- 其他权限仅用于通知、开机恢复提醒和用户确认后的应用更新。
- 提醒使用省电的非精确闹钟调度，系统可能根据设备状态小幅延后。

## 设计稿

三个原始方向保留在 `designs/directions/index.html`，当前实现采用 **A · 温柔手帐**。
