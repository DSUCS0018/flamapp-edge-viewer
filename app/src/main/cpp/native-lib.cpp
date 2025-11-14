#include <jni.h>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <vector>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, \"native-lib\", __VA_ARGS__)

using namespace cv;

extern \"C\"
JNIEXPORT void JNICALL
Java_com_flamapp_edge_NativeBridge_processFrame(
        JNIEnv *env,
        jobject /*thiz*/,
        jbyteArray nv21Arr,
        jint width,
        jint height) {

    if (nv21Arr == nullptr) {
        LOGD(\"processFrame: nv21Arr is null\");
        return;
    }

    jbyte *nv21 = env->GetByteArrayElements(nv21Arr, nullptr);
    if (nv21 == nullptr) {
        LOGD(\"processFrame: failed to get byte array elements\");
        return;
    }

    // Create YUV Mat from NV21 data (height + height/2 rows)
    Mat yuv(height + height/2, width, CV_8UC1, (unsigned char *)nv21);

    // Convert to BGR
    Mat bgr;
    try {
        cvtColor(yuv, bgr, COLOR_YUV2BGR_NV21);
    } catch (const cv::Exception& e) {
        LOGD(\"OpenCV cvtColor exception: %s\", e.what());
        env->ReleaseByteArrayElements(nv21Arr, nv21, JNI_ABORT);
        return;
    }

    // Convert to grayscale
    Mat gray;
    cvtColor(bgr, gray, COLOR_BGR2GRAY);

    // Smooth and Canny edges
    Mat blurred, edges;
    GaussianBlur(gray, blurred, Size(5,5), 1.5);
    Canny(blurred, edges, 50, 150);

    // For debugging, log size
    LOGD(\"native processed frame %dx%d, edges size: %dx%d\", width, height, edges.cols, edges.rows);

    // NOTE: we are not returning the image to Java in this commit.
    // In the next task we will convert 'edges' to RGBA and provide a path to upload to a GL texture or return bytes.

    // Release JNI array
    env->ReleaseByteArrayElements(nv21Arr, nv21, JNI_ABORT);
}

extern \"C\"
JNIEXPORT void JNICALL
Java_com_flamapp_edge_NativeBridge_initOpenCVNative(
        JNIEnv *env,
        jobject /*thiz*/,
        jstring assetDir) {
    // This native init is a placeholder. If you need to load files, use assetDir.
    LOGD(\"initOpenCVNative called\");
}
