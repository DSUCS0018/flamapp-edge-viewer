package com.flamapp.edge

object NativeBridge {
    init {
        try {
            System.loadLibrary(\"native-lib\")
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    external fun initOpenCVNative(assetDir: String?)
    external fun processFrame(nv21: ByteArray, width: Int, height: Int)

    // New APIs to fetch processed RGBA bytes and dimensions
    external fun getProcessedRgba(): ByteArray?
    external fun getProcessedWidth(): Int
    external fun getProcessedHeight(): Int
}
