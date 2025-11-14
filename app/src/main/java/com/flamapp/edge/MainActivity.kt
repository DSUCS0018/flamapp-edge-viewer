package com.flamapp.edge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var glView: GLRenderer
    private lateinit var cameraHelper: Camera2Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = GLRenderer(this)
        setContentView(glView)

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                setupCamera()
            } else {
                Log.e(\"MainActivity\", \"Camera permission denied\")
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupCamera() {
        cameraHelper = Camera2Helper(this, object : Camera2Helper.FrameCallback {
            override fun onFrameAvailable(nv21: ByteArray, width: Int, height: Int) {
                // pass to native processing
                try {
                    NativeBridge.processFrame(nv21, width, height)
                } catch (t: Throwable) {
                    Log.e(\"MainActivity\", \"native process error\", t)
                }
            }
        })
        // start immediately (Camera2Helper.startCamera opens camera)
        cameraHelper.startCamera()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
        if (::cameraHelper.isInitialized) cameraHelper.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }
}
