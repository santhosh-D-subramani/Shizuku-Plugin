# shizuku_api
A  Flutter Plugin that gives access to shizuku api.

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
