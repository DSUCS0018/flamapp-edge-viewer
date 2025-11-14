package com.flamapp.edge

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.util.Log
import android.util.Size
import android.view.TextureView
import androidx.core.app.ActivityCompat

class Camera2Helper(private val ctx: Context, private val cb: FrameCallback) {

    interface FrameCallback {
        fun onFrameAvailable(nv21: ByteArray, width: Int, height: Int)
    }

    private val cameraManager = ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
            startCamera()
        }
        override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}
        override fun onSurfaceTextureDestroyed(st: SurfaceTexture) = true
        override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
    }

    fun startCamera() {
        try {
            val cameraId = cameraManager.cameraIdList.first()
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            val chosen = chooseSize(configMap.getOutputSizes(ImageReader::class.java).toList())
            imageReader = ImageReader.newInstance(chosen.width, chosen.height, android.graphics.ImageFormat.YUV_420_888, 2)
            imageReader?.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                try {
                    val nv21 = ImageUtil.imageToNV21(image)
                    cb.onFrameAvailable(nv21, image.width, image.height)
                } catch (t: Throwable) {
                    Log.e(\"Camera2Helper\", \"frame processing error: \", t)
                } finally {
                    image.close()
                }
            }, null)

            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // permission should be requested by Activity before calling startCamera
                return
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    val surface = imageReader!!.surface
                    val requestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    requestBuilder.addTarget(surface)
                    camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            captureSession = session
                            requestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                            session.setRepeatingRequest(requestBuilder.build(), null, null)
                        }
                        override fun onConfigureFailed(session: CameraCaptureSession) {}
                    }, null)
                }
                override fun onDisconnected(camera: CameraDevice) {}
                override fun onError(camera: CameraDevice, error: Int) {}
            }, null)

        } catch (e: Exception) {
            Log.e(\"Camera2Helper\", \"startCamera exception\", e)
        }
    }

    fun stopCamera() {
        captureSession?.close(); captureSession = null
        cameraDevice?.close(); cameraDevice = null
        imageReader?.close(); imageReader = null
    }

    private fun chooseSize(sizes: List<Size>): Size {
        // prefer 1280x720 if available else take first
        for (s in sizes) {
            if (s.width == 1280 && s.height == 720) return s
        }
        return sizes.first()
    }
}
