# shizuku_api
A  Flutter Plugin that gives access to shizuku api.

# info 
- This plugin is built for my Playstore Application [System App Remover](https://play.google.com/store/apps/details?id=com.santhoshDsubramani.systemappremover),which is built to delete system apps(bloatwares) without root or computer.
- Im not good in Java/ Kotlin, Somehow done this plugin if you find any improvements? check out my github 

# Install
```
  
  flutter pub add shizuku_api
  
  ```

# Requirements
- [Shizuku](https://shizuku.rikka.app/) app should be installed and running

- app/build.gradle
  - minSdk should be >= 24



- In AndroidManifest.xml add this inside application tag
-  ```
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
# Usage

- Important: DO THIS BEFORE CALLING ANY OTHER 
- check if Shizuku is running with this before using other calls
- 
- ```
  bool isBinderRunning = await _shizukuApiPlugin.pingBinder() ?? false;
  
  ```

- Request Shizuku Access
  - !! [Shizuku](https://shizuku.rikka.app/) should be installed and running
  - ```
    final _shizukuApiPlugin = ShizukuApi();
    bool requestPermission = await  _shizukuApiPlugin.checkPermission(); // triggers shizuku popup
    print(requestPermission); // if allowed returns true else false
    ```
- Run Commands
  - ! root environment(su) is not tested 
  - can run basic ADB commands (working fine)
  - ```
    String command = 'ls';
    await _shizukuApiPlugin.runCommand(command); // returns all folders as List<String>
    ```

