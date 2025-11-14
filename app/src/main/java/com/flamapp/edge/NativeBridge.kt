package com.flamapp.edge

object NativeBridge {
    init {
        try {
            System.loadLibrary(\"native-lib\")
        } catch (t: Throwable) {
            // native lib may not be built yet during early commits
        }
    }

    external fun initOpenCVNative(assetDir: String?)
    external fun processFrame(nv21: ByteArray, width: Int, height: Int)
}
