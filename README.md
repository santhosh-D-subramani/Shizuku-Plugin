# 🔌 Shizuku API Flutter Plugin  

Access the **Shizuku API** seamlessly in your Flutter apps! 🚀  


## 🌟 About  
This plugin powers my Play Store application [**System App Remover**](https://play.google.com/store/apps/details?id=com.santhoshDsubramani.systemappremover), which allows users to remove system apps (*bloatware*) effortlessly without requiring root access or a computer(Android 10 and below still needs computer to run Shizuku).


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

📝 **AndroidManifest.xml** add this inside application tag:

``` xml
   <application>
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

- ⚠️ **Important:** DO THIS BEFORE CALLING ANY OTHER PLUGIN FEATURES
- !! [Shizuku](https://shizuku.rikka.app/) should be installed
- ✅ Check if **Shizuku** is running first

``` dart
  bool isBinderRunning = await _shizukuApiPlugin.pingBinder() ?? false; // tries to ping shizuku
  
  ```

- 🛠️ **check Shizuku Permission**
  
  ``` dart
    final _shizukuApiPlugin = ShizukuApi();

    // checks if shizuku permission granted by user
    //returns true if previously allowed permission or false if permission declined /never requested
    bool checkPermission = await  _shizukuApiPlugin.checkPermission();

    print(checkPermission);
  
    ```
- 🛠️ **request Shizuku Permission**

  ``` dart
    final _shizukuApiPlugin = ShizukuApi();
  
    // triggers shizuku popup
    //returns true if Permission allowed or false if declined
    bool requestPermission = await  _shizukuApiPlugin.requestPermission(); 
  
    print(requestPermission);
  
    ```  
- 💻 **Run Commands**
  - ⚡ **Root environment (su)** is not tested
  - ✅ Can run **ADB shell commands** (working fine)
    
  ``` dart
    String command = 'pm uninstall --user 0 com.android.chrome';
    await _shizukuApiPlugin.runCommand(command); // returns success if Uninstalled system app / Failure if failed
    ```

