package com.sdk.cameraandgallery

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.sdk.cameraandgallery.databinding.ActivityTakeScreenShotBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class TakeScreenShotActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTakeScreenShotBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.takeScreen.setOnClickListener {
            val bitmap: Bitmap? = getScreenShot(window.decorView.rootView)
            if (bitmap != null) {
                Handler(Looper.myLooper()!!).postDelayed({
                    binding.imageView.setImageBitmap(bitmap)
                    Toast.makeText(this, "Now took", Toast.LENGTH_SHORT).show()
                }, 300)
                saveToGallery(bitmap)
            }
        }
    }

    private fun saveToGallery(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, fileName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    private fun getScreenShot(view: View): Bitmap? {
        val returnBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnBitmap)
        val background = view.background
        if (background != null)
            background.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnBitmap
    }
}