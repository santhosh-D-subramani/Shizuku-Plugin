# 🔌 Shizuku API Flutter Plugin  

Access the **Shizuku API** seamlessly in your Flutter apps! 🚀  


## 🌟 About  
This plugin powers my Play Store application [**System App Remover**](https://play.google.com/store/apps/details?id=com.santhoshDsubramani.systemappremover), which allows users to remove system apps (*bloatware*) effortlessly without requiring root access or a computer.


## ⚡ Installation  
Add the plugin to your project:  
```
  
  flutter pub add shizuku_api
  
  ```

## 📋 Requirements
- 📱 [**Shizuku**](https://shizuku.rikka.app/) app should be installed and running

## 🔧 Configuration
📝 **app/build.gradle**
  - minSdk should be >= 24

- In **AndroidManifest.xml** add this inside application tag:

```
   <application>
   <!-- other code>

   
    <provider
            android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:multiprocess="false"
            android:enabled="true"
            android:exported="true"
            android:permission="android.permission.INTERACT_ACROSS_USERS_FULL" />
   </application>
   ```
# 🚀 Usage

- ⚠️ **Important:** DO THIS BEFORE CALLING ANY OTHER
- ✅ Check if **Shizuku** is running first

```
  bool isBinderRunning = await _shizukuApiPlugin.pingBinder() ?? false;
  
  ```

- 🛠️ **Request Shizuku Access**
  - !! [Shizuku](https://shizuku.rikka.app/) should be installed and running

  ```
    final _shizukuApiPlugin = ShizukuApi();
    bool requestPermission = await  _shizukuApiPlugin.checkPermission(); // triggers shizuku popup
    print(requestPermission); // if allowed returns true else false
    ```
- 💻 **Run Commands**
  - ⚡ **Root environment (su)** is not tested
  - ✅ Can run **ADB shell commands** (working fine)
  ```
    String command = 'pm uninstall --user 0 com.android.chrome';
    await _shizukuApiPlugin.runCommand(command); // returns success if Uninstalled system app / Failure if failed
    ```

