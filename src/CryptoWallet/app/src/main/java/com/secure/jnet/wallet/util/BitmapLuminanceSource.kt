//package com.secure.jnet.wallet.util
//
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.graphics.Matrix
//import com.google.zxing.LuminanceSource
//
//class BitmapLuminanceSource(private val bitmap: Bitmap) : LuminanceSource(bitmap.width, bitmap.height) {
//
//    override fun getRow(y: Int, row: ByteArray): ByteArray {
//        val width = width
//        if (y < 0 || y >= height) {
//            throw IllegalArgumentException("Requested row is outside the image: $y")
//        }
//        val source = IntArray(width)
//        bitmap.getPixels(source, 0, width, 0, y, width, 1)
//        for (x in 0 until width) {
//            row[x] = source[x].toByte()
//        }
//        return row
//    }
//
//    override fun getMatrix(): ByteArray {
//        val width = width
//        val height = height
//        val area = width * height
//        val matrix = ByteArray(area)
//        val pixels = IntArray(area)
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
//        for (y in 0 until height) {
//            val offset = y * width
//            for (x in 0 until width) {
//                val pixel = pixels[offset + x]
//                val luminance = (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(
//                    pixel
//                )).toInt()
//                matrix[offset + x] = luminance.toByte()
//            }
//        }
//        return matrix
//    }
//
//    override fun isCropSupported(): Boolean {
//        return true
//    }
//
//    override fun crop(left: Int, top: Int, width: Int, height: Int): LuminanceSource {
//        val newBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
//        return BitmapLuminanceSource(newBitmap)
//    }
//
//    override fun isRotateSupported(): Boolean {
//        return true
//    }
//
//    override fun invert(): LuminanceSource {
//        // Doesn't need to be implemented for our purposes
//        throw UnsupportedOperationException("This luminance source does not support inversion")
//    }
//
//    override fun rotateCounterClockwise(): LuminanceSource {
//        val rotatedBitmap = rotateBitmap(bitmap, -90f)
//        return BitmapLuminanceSource(rotatedBitmap)
//    }
//
//    private fun rotateBitmap(src: Bitmap, degree: Float): Bitmap {
//        val matrix = Matrix()
//        matrix.postRotate(degree)
//        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
//    }
//}