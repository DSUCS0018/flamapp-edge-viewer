package com.flamapp.edge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var cameraHelper: Camera2Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textureView = TextureView(this)
        setContentView(textureView)

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
                // For now just log a short message for each frame (avoid flooding logs)
                Log.d(\"MainActivity\", \"frame received: {nv21.size} bytes, width x height\")
                // Later: call NativeBridge.processFrame(nv21, width, height)
            }
        })
        textureView.surfaceTextureListener = cameraHelper.textureListener
    }

    override fun onPause() {
        super.onPause()
        if (::cameraHelper.isInitialized) cameraHelper.stopCamera()
    }
}
