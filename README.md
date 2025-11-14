# Flamapp.AI — Edge Detection Viewer (Android + OpenCV + JNI + Web)

This repository contains my submission for the Flamapp.AI First-Round Assignment.  
It implements a real Android + JNI + C++ + OpenCV pipeline, along with a minimal web viewer, following all submission guidelines.

---

# ⭐ What This Project Actually Does (Clear Overview)

This project implements a **real-time camera edge detection pipeline** on Android using:

- **Camera2 API**
- **JNI (Java → C++ → Java bridge)**
- **OpenCV (C++ native image processing)**
- **OpenGL ES (live rendering of processed frames)**

Pipeline:

Additionally, a **Web Viewer** is included to showcase processed output (`web/index.html`).

---

# 🔍 Architecture (Simple Explanation)

### 1️⃣ Camera2 (Android/Kotlin)  
File: `Camera2Helper.kt`
- Captures frames in YUV_420_888
- Converts to NV21
- Sends NV21 byte array to JNI

### 2️⃣ JNI Bridge (Android/Kotlin)  
File: `NativeBridge.kt`
- Loads the native library (`native-lib`)
- Exposes native functions:
  - `processFrame(nv21, width, height)`
  - `getProcessedRgba()`
  - `getProcessedWidth()`
  - `getProcessedHeight()`

### 3️⃣ Native C++ Processing (OpenCV)  
File: `native-lib.cpp`
- Converts NV21 → BGR using `cvtColor`
- Converts BGR → Gray → GaussianBlur → Canny
- Converts Canny (1-channel) → RGBA (4-channel)
- Stores final RGBA in a global buffer
- JNI getters expose processed frame back to Android

### 4️⃣ OpenGL ES Renderer (Android/Kotlin)  
File: `GLRenderer.kt`
- Creates a texture
- Calls `NativeBridge.getProcessedRgba()`
- Uploads RGBA image to the texture
- Draws it on screen every frame

### 5️⃣ Main Activity  
File: `MainActivity.kt`
- Requests camera permission
- Starts camera
- Sends every frame to native C++ via JNI
- Displays processed edges through GLRenderer

### 6️⃣ Build Scripts  
File: `CMakeLists.txt`
- Imports OpenCV native `.so` files for all ABIs
- Compiles and links `native-lib.cpp`

### 7️⃣ Web Viewer  
Folder: `web/`
- Contains `index.html` + TypeScript viewer
- Displays a sample output image (placeholder or real screenshot)

---

# 📁 Project Structure

