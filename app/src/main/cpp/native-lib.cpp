#include <jni.h>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <vector>
#include <mutex>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, \"native-lib\", __VA_ARGS__)

using namespace cv;
using std::vector;

static std::vector<unsigned char> g_rgba_buffer;
static int g_width = 0;
static int g_height = 0;
static std::mutex g_mutex;

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

    // Convert NV21 to Mat
    Mat yuv(height + height/2, width, CV_8UC1, (unsigned char *)nv21);
    Mat bgr;
    try {
        cvtColor(yuv, bgr, COLOR_YUV2BGR_NV21);
    } catch (const cv::Exception& e) {
        LOGD(\"OpenCV cvtColor exception: %s\", e.what());
        env->ReleaseByteArrayElements(nv21Arr, nv21, JNI_ABORT);
        return;
    }

    Mat gray;
    cvtColor(bgr, gray, COLOR_BGR2GRAY);

    Mat blurred, edges;
    GaussianBlur(gray, blurred, Size(5,5), 1.5);
    Canny(blurred, edges, 50, 150);

    // Convert edges (single channel) to RGBA so Java can upload it as a texture
    Mat rgba;
    cvtColor(edges, rgba, COLOR_GRAY2RGBA);

    // Lock and store the rgba data in global buffer
    {
        std::lock_guard<std::mutex> lock(g_mutex);
        g_width = rgba.cols;
        g_height = rgba.rows;
        size_t bufSize = (size_t)g_width * (size_t)g_height * 4;
        g_rgba_buffer.resize(bufSize);
        memcpy(g_rgba_buffer.data(), rgba.data, bufSize);
    }

    LOGD(\"Processed frame stored: %dx%d (RGBA bytes=%zu)\", g_width, g_height, g_rgba_buffer.size());

    // Release JNI array
    env->ReleaseByteArrayElements(nv21Arr, nv21, JNI_ABORT);
}

extern \"C\"
JNIEXPORT jbyteArray JNICALL
Java_com_flamapp_edge_NativeBridge_getProcessedRgba(
        JNIEnv *env,
        jobject /*thiz*/) {

    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_rgba_buffer.empty()) return nullptr;

    jbyteArray out = env->NewByteArray((jsize)g_rgba_buffer.size());
    if (out == nullptr) return nullptr;
    env->SetByteArrayRegion(out, 0, (jsize)g_rgba_buffer.size(), reinterpret_cast<const jbyte*>(g_rgba_buffer.data()));
    return out;
}

extern \"C\"
JNIEXPORT jint JNICALL
Java_com_flamapp_edge_NativeBridge_getProcessedWidth(JNIEnv *env, jobject /*thiz*/) {
    std::lock_guard<std::mutex> lock(g_mutex);
    return g_width;
}

extern \"C\"
JNIEXPORT jint JNICALL
Java_com_flamapp_edge_NativeBridge_getProcessedHeight(JNIEnv *env, jobject /*thiz*/) {
    std::lock_guard<std::mutex> lock(g_mutex);
    return g_height;
}

extern \"C\"
JNIEXPORT void JNICALL
Java_com_flamapp_edge_NativeBridge_initOpenCVNative(JNIEnv *env, jobject /*thiz*/, jstring assetDir) {
    LOGD(\"initOpenCVNative called\");
}
