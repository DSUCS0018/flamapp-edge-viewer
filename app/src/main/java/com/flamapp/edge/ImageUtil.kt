package com.flamapp.edge

import android.media.Image
import java.nio.ByteBuffer

object ImageUtil {
    // Converts Image (YUV_420_888) to NV21 byte array
    fun imageToNV21(image: Image): ByteArray {
        val width = image.width
        val height = image.height
        val ySize = width * height
        val uvSize = width * height / 4

        val nv21 = ByteArray(ySize + 2 * uvSize)

        val yBuffer: ByteBuffer = image.planes[0].buffer
        val uBuffer: ByteBuffer = image.planes[1].buffer
        val vBuffer: ByteBuffer = image.planes[2].buffer

        val rowStrideY = image.planes[0].rowStride
        val rowStrideU = image.planes[1].rowStride
        val rowStrideV = image.planes[2].rowStride

        // copy Y
        var pos = 0
        if (rowStrideY == width) {
            yBuffer.get(nv21, 0, ySize)
            pos += ySize
        } else {
            val yRow = ByteArray(rowStrideY)
            for (row in 0 until height) {
                yBuffer.get(yRow, 0, rowStrideY)
                System.arraycopy(yRow, 0, nv21, pos, width)
                pos += width
            }
        }

        // U and V are swapped depending on camera (but for NV21 we need VU order)
        val chromaHeight = height / 2
        val chromaWidth = width / 2

        val uRow = ByteArray(rowStrideU)
        val vRow = ByteArray(rowStrideV)

        var uvPos = ySize
        for (row in 0 until chromaHeight) {
            // read V then U for NV21 (V then U = VU)
            vBuffer.get(vRow, 0, rowStrideV)
            uBuffer.get(uRow, 0, rowStrideU)
            var col = 0
            while (col < chromaWidth) {
                nv21[uvPos++] = vRow[col]
                nv21[uvPos++] = uRow[col]
                col++
            }
        }
        return nv21
    }
}
