package com.example.cameraandgallery.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.cameraandgallery.BuildConfig
import com.example.cameraandgallery.databinding.ActivityCameraBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

class CameraActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }
    private lateinit var currentImagePath:String
    private lateinit var photoUri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnOldMethod.setOnClickListener {
            cameraOld()
        }
        binding.btnNewMethod.setOnClickListener {
            photoUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID,
                createImageFile()
            )
            pickImageFromCamera.launch(photoUri)
        }
        binding.btnDelete.setOnClickListener {
            clearCamera()
        }
    }

    private fun cameraOld(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        val photoFile = createImageFile()
        photoFile.also {
            val photoUri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID, it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
            startActivityForResult(intent,100)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val format = SimpleDateFormat("yyyy MMdd HHmm ss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_$format", ".jpg", storageDir)
        currentImagePath = file.absolutePath
        return file
    }

    private fun clearCamera() {
        binding.imageView.setImageURI(null)
        if (filesDir.isFile) {
            val listFiles = filesDir.listFiles() ?: emptyArray()

            for (i in listFiles) {
                i.delete()
                Log.d("@@@@", i.absolutePath)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::currentImagePath.isInitialized){
            binding.imageView.setImageURI(Uri.fromFile(File(currentImagePath)))
        }
    }

    private val pickImageFromCamera = registerForActivityResult(ActivityResultContracts.TakePicture()){
        if (it){
            binding.imageView.setImageURI(photoUri)
            val contentResolver = contentResolver?.openInputStream(photoUri)
            val file = File(filesDir,"imageView.jpg")
            val fileOutputStream = FileOutputStream(file)
            contentResolver?.copyTo(fileOutputStream)
            contentResolver?.close()
            fileOutputStream.close()
            Log.d("AbsolutePath",file.absolutePath)

        }
    }
}