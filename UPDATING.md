# 发布与应用内更新

## 发布地址

- 下载页：https://honey2306.github.io/xiaojianchi/
- 最新版本：https://github.com/honey2306/xiaojianchi/releases/latest
- 更新清单：https://github.com/honey2306/xiaojianchi/releases/latest/download/update.json
- 备用更新清单：https://honey2306.github.io/xiaojianchi/update.json

## 用户更新流程

1. App 启动时静默检查，或进入「设置 → 检查更新」。
2. 服务端 `versionCode` 大于本机版本时，展示版本号和 RELEASE_NOTES。
3. 用户点击下载，App 显示下载进度。
4. APK 下载完成后必须匹配清单中的 SHA-256，否则拒绝安装。
5. 用户确认后交给 Android 系统安装器；首次需要允许「小坚持」安装应用。
6. Android 同时验证包名和签名，签名不一致的旧包无法覆盖安装。

不会自动安装，也不会静默替换 App。

## 发布新版本

1. 在 `app/build.gradle.kts` 同时递增 `versionCode` 和更新 `versionName`。
2. 更新 `RELEASE_NOTES.md`，运行 `./gradlew testDebugUnitTest lintDebug assembleDebug`。
3. 提交并推送 main。
4. 创建同名标签，例如版本 `1.0.1`：

   ```bash
   git tag -a v1.0.1 -m "小坚持 1.0.1"
   git push origin v1.0.1
   ```

5. `Build and publish APK` workflow 会测试、签名、生成 SHA-256 和 `update.json`，再发布 Release。
6. Release 发布后，`Deploy download page` workflow 会同步备用清单和下载页。

## 签名安全

- 唯一发布签名密钥只保存在 GitHub Actions Secrets，不进入 Git 仓库。
- 本地原始密钥位于被忽略的 `.signing/`，必须另做离线备份；丢失后将无法给老用户提供覆盖更新。
- Debug 与 Release 使用不同签名，从 Debug 切换到正式版需要先卸载 Debug；卸载会清除本机数据。
- 更新包除了 SHA-256 校验，还依赖 Android 包管理器验证相同签名，防止不同来源的 APK 覆盖。
