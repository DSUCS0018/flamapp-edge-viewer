# Flamapp.AI — Edge Detection Viewer (Android + OpenCV + JNI + Web)

This repository contains my submission for the Flamapp.AI first round assignment (Android + native OpenCV processing + Web viewer).

## What is included (initial)
- Project scaffold and README
- Will implement: Camera2 → JNI → OpenCV (Canny) → OpenGL ES renderer (Android)
- Minimal TypeScript web viewer to display processed frames

## Commit history
This repo will be committed incrementally (small focused commits), per the assignment guidelines.

## How to build & run (developer notes)

### Option A — (Recommended for reviewers) — No build required
This repository contains a full Android + native C++ (OpenCV) implementation and a minimal web viewer.  
If you don't want to build the APK, reviewers can inspect the code, compile the native module and/or open web/index.html to view a sample processed image.

### Option B — Build APK with Android Studio (optional)
1. Install Android Studio.
2. From SDK Manager install: **Android SDK**, **NDK (side-by-side)**, **CMake**, **LLDB**.
3. Open this project folder in Android Studio.
4. Sync Gradle. If prompted, accept installation of missing SDK components.
5. Build & Run on a physical device (recommended) or emulator.
   - The app requests CAMERA permission at runtime.
   - The pipeline: Camera2 → JNI → OpenCV (Canny) → GLSurfaceView rendering.

### Notes about OpenCV
- I included the OpenCV prebuilt libraries under pp/src/main/cpp/opencv (ABI folders + include).
- CMakeLists imports the correct libopencv_java4.so for the current ANDROID_ABI.

## What to submit
1. Public GitHub repo URL with commit history (this repo).  
2. README.md with setup notes (this file).  
3. screenshot(s) or GIF(s) added to web/sample.png (or referenced in README).

