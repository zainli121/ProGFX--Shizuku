Pro GFX Shizuku - GitHub Build Version
--------------------------------------
What this zip contains:
- Android Studio project skeleton configured for Android 12+ using Jetpack Compose.
- Shizuku API dependencies added (dev.rikka.shizuku:api:13.1.5).
- MainActivity with permission checks and placeholder apply logic.
- GitHub Actions workflow to build a debug APK when pushed to 'main' branch.

How to build via GitHub (mobile):
1. Create a new GitHub repository (name: ProGFX-Shizuku or similar).
2. Extract this ZIP on your phone (use any file manager that can extract).
3. Upload all files/folders to the repo root via GitHub website or GitHub mobile app.
4. Commit/push to 'main'.
5. Open 'Actions' tab in GitHub and wait for workflow run to finish.
6. Download artifact 'app-debug-apk' from the workflow run page.

Notes:
- You may need to open the project in Android Studio at least once (on a PC) to generate Gradle wrapper if GitHub Actions fails because wrapper missing.
- Test commands are placeholders. Replace the 'settings put global ...' commands with exact commands that work on your device for PUBG.
