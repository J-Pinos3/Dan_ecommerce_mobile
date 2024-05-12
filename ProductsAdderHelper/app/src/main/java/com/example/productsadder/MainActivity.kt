package com.example.productsadder

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.productsadder.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.google.firebase.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.kotlin.colorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()

    private val fireStore = Firebase.firestore
    private val productsStorage = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Product color")
                .setPositiveButton("Select", object: ColorEnvelopeListener{
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)

                            updateColors()
                        }
                    }
                })
                .setNegativeButton("Cancel"){
                    colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }


        val selectImagesACtivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                val intent = result.data

                //user selects multiple images
                if(intent?.clipData != null){
                    val count = intent.clipData?.itemCount ?: 0
                    (0 until count).forEach {index->
                        val imageUri = intent.clipData?.getItemAt(index)?.uri
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }
                }else{
                    //user chose just one image
                    val imageURi = intent?.data
                    imageURi?.let {
                        selectedImages.add(it)
                    }
                }
                updateImages()
            }
        }


        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesACtivityResult.launch(intent)
        }

    }



    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }


    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors= "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.saveProduct){
            val productValidation = validateInformation()
            if(!productValidation){
                Toast.makeText(this,"Please complete all fields.",Toast.LENGTH_LONG)
                return false
            }


            saveProduct()
            clearInputs()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    private fun clearInputs(){
        binding.edName.text = "".toEditable()
        binding.edCategory.text = "".toEditable()
        binding.edDescription.text = "".toEditable()
        binding.edPrice.text = "".toEditable()
        binding.offerPercentage.text = "".toEditable()
        binding.edSizes.text = "".toEditable()

        binding.tvSelectedColors.text = ""
        selectedColors.clear()

        binding.tvSelectedImages.text = ""
        selectedImages.clear()
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        var description = binding.edDescription.text.toString().trim()//?
        val price = binding.edPrice.text.toString().trim().toFloat()
        val offerPercentage = binding.offerPercentage.text.toString().trim()//?
        val sizes =  getSizesList(binding.edSizes.text.toString())//?
        val imagesBytesArray = getImagesBytesArray()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                showLoading()
            }
            try {
                async {
                    imagesBytesArray.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = productsStorage.child("products/images/$id")
                            val result =  imageStorage.putBytes(it).await()
                            val downloadURL = result.storage.downloadUrl.await().toString()
                            images.add(downloadURL)
                        }
                    }
                }.await()//await = await for the code inside to finish its execution
            }catch (e: Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    showLoading()
                }

            }

            val product = Product(
                UUID.randomUUID().toString(),
                name, category,price,
                if( offerPercentage.isNullOrEmpty() ) null else offerPercentage.toFloat(),
                if( description.isNullOrEmpty() ) null else description,
                if( selectedColors.isNullOrEmpty() ) null else selectedColors,
                if( sizes.isNullOrEmpty() ) emptyList() else sizes ,
                images
            )

            fireStore.collection("Products").add(product).addOnSuccessListener {

                    hideLoading()
                }.addOnFailureListener{
                    hideLoading()
                    Log.e("ERROR ",it.message.toString())
                }

        }

    }


    private fun hideLoading() {
        binding.pbUploadProduct.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.pbUploadProduct.visibility = View.VISIBLE
    }


    private fun getImagesBytesArray(): List<ByteArray> {
        val imagesByteArray= mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val imageBmp= MediaStore.Images.Media.getBitmap(contentResolver, it)
            if(imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream) ){
                imagesByteArray.add(stream.toByteArray())
            }
        }

        return imagesByteArray
    }


    private fun getSizesList(sizes: String?): List<String>{
        return  sizes?.split(",") ?: emptyList<String>()
    }


    private fun validateInformation(): Boolean {
        if(binding.edPrice.text.toString().trim().isEmpty() )
            return false

        if( binding.edName.text.toString().trim().isEmpty() )
            return false

        if( binding.edCategory.text.toString().trim().isEmpty() )
            return false

        if( selectedImages.isEmpty() )
            return false

        return true
    }

}