package com.dicoding.picodiploma.loginwithanimation.view.uploadstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultData
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoriesBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityUploadStoryBinding
import com.dicoding.picodiploma.loginwithanimation.di.getImageUri
import com.dicoding.picodiploma.loginwithanimation.di.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.di.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted: Boolean ->
            if (isGranted){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun permissionGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tell Your Story"
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (!permissionGranted()){
            requestPermission.launch(REQUIRED_PERMISSION)
        }

        binding.gallery.setOnClickListener { startGallery() }
        binding.camera.setOnClickListener { startCamera() }
        binding.AddButton.setOnClickListener { uploadImage() }

    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        } else{
            Log.d("Photo Pick", "No Item Selected")
        }
    }
    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){isSuccess ->
        if (isSuccess){
            showImage()
        }
    }

    private fun startCamera(){
        val uri = getImageUri(this)
        if (uri != null){
            currentImageUri = uri
            launcherCamera.launch(uri)
        } else{
            Toast.makeText(this, "Fail to get Image", Toast.LENGTH_LONG).show()
        }
    }

    private fun showImage(){
        val uri = currentImageUri
        if (uri != null){
            Log.d("Image URI", "showImage: $uri")
            binding.uploadImage.setImageURI(uri)
        } else{
            Log.d("Image URI", "No Image Found")
        }
    }

    private fun uploadImage(){
        val uri = currentImageUri
        if (uri != null){
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "Image path: ${imageFile.path}")
            val desc = binding.edAddDescription.text.toString()

            viewModel.getSession().observe(this){story ->
                val token = story.token
                viewModel.uploadImage(token, imageFile, desc).observe(this){result ->
                    if (result != null){
                        when (result){
                            is ResultData.Loading -> showLoading(true)
                            is ResultData.Success ->{
                                showToast("Token is Null")
                                showLoading(false)
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            is ResultData.Error->{
                                showToast(result.error)
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}