# Flamapp.AI — Edge Detection Viewer (Android + OpenCV + JNI + Web)

This repository contains my submission for the Flamapp.AI first-round interview assignment.

It includes a complete Android + Native C++ OpenCV processing pipeline along with a minimal Web viewer, following the instructions provided in the assignment document.

---

# ⭐ Features Implemented

### ✅ **Android (Kotlin + JNI + OpenCV)**
- Camera2 frame capture (YUV_420_888 → NV21).
- JNI bridge (`NativeBridge`) for passing frames to the native layer.
- Native C++ (OpenCV) processing:
  - NV21 → BGR → Grayscale
  - Gaussian blur
  - Canny edge detection
  - Converted to RGBA and stored in global buffer.
- JNI getters expose processed RGBA frame back to the Android layer.
- Real-time rendering using **OpenGL ES 2.0 GLSurfaceView**.
- Clean frame-flow pipeline:
