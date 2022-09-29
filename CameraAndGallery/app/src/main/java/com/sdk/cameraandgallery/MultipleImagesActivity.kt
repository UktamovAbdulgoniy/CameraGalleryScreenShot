package com.sdk.cameraandgallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.sdk.cameraandgallery.adapter.ImagesAdapter
import com.sdk.cameraandgallery.databinding.ActivityMultipleImagesBinding

class MultipleImagesActivity : AppCompatActivity() {
    private val imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var imagesAdapter: ImagesAdapter
    private val binding by lazy { ActivityMultipleImagesBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imagesAdapter = ImagesAdapter()
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MultipleImagesActivity, 2)
            adapter = imagesAdapter
        }

        binding.btnPick.setOnClickListener {
            getMultipleImagesOld()
        }
    }

    private fun getMultipleImagesOld() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                for (image in 0 until data.clipData?.itemCount!!) {
                    imageUris.add(data.clipData?.getItemAt(image)?.uri!!)
                }
                binding.textCount.text = imageUris.size.toString()
                imagesAdapter.imageUris = imageUris
                imagesAdapter.notifyDataSetChanged()
            } else if (data?.data != null) {
                imageUris.add(Uri.parse(data.data?.path))
            }
        }
    }
}