Shizuku integration notes (for Pro GFX Shizuku)
------------------------------------------------

1) Dependency:
   The project already includes Shizuku artifacts in app/build.gradle:
     implementation 'dev.rikka.shizuku:api:13.1.5'
     implementation 'dev.rikka.shizuku:aidl:13.1.5'
     implementation 'dev.rikka.shizuku:provider:13.1.5'

   If a newer version is available, replace the version number accordingly.

2) Permission flow (high level):
   - Ask user to install Shizuku Manager (Play Store).
   - Start Shizuku server (user taps Start in Shizuku app or uses adb command).
   - In your app, call Shizuku.checkSelfPermission() to see if permission is granted.
   - If not granted, call Shizuku.requestPermission(REQUEST_CODE) which triggers the Shizuku manager to show authorization prompt.

3) Executing shell commands:
   - Shizuku provides APIs to execute shell commands or call system services with elevated identity.
   - Refer to official Shizuku-API README for exact shell/exec usage:
     https://github.com/RikkaApps/Shizuku-API

4) Example (concept):
   After permission is granted, you can invoke shell commands via Shizuku similarly to:
     val cmd = arrayOf("sh", "-c", "settings put global some_key some_value")
     // Use Shizuku's process APIs to run cmd and read output.

5) Debugging:
   - If GitHub Actions build fails due to Gradle wrapper, open project in Android Studio and generate wrapper, or replace gradle wrapper files with proper ones.
   - Test on a device with Shizuku installed. Start Shizuku before launching the app.

6) Security & Ethics:
   - Do not publish apps that misuse system APIs. This project is for personal use only as requested.

References:
- Shizuku GitHub: https://github.com/RikkaApps/Shizuku
- Shizuku-API: https://github.com/RikkaApps/Shizuku-API
- Shizuku docs: https://shizuku.rikka.app/
