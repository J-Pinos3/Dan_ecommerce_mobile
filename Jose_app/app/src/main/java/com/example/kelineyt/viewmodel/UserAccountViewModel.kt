package com.example.kelineyt.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.KelineApplication
import com.example.kelineyt.data.User
import com.example.kelineyt.util.Constants.USER_COLLECTION
import com.example.kelineyt.util.RegisterValidation
import com.example.kelineyt.util.Resource
import com.example.kelineyt.util.validateEmail
import com.example.kelineyt.util.validatePassword
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
): AndroidViewModel(app) {

    private val _user = MutableStateFlow< Resource<User> >(Resource.Unspecified())
     val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow< Resource<User> >(Resource.Unspecified())
     val updateInfo = _updateInfo.asStateFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    init {
        getUser()
    }

    fun getUser(){
        viewModelScope.launch { _user.emit(Resource.Loading()) }
        firestore.collection(USER_COLLECTION)
            .document(auth.uid!!)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    fun updateUser(user: User, imageUri: Uri?){
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if(!areInputsValid){
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error("All fields have to be completed"))
            }
        }
        viewModelScope.launch { _updateInfo.emit(Resource.Loading()) }

        //imageUri will bve null if user havent changed the image
        //and imageUri wont be null if the user changed the image
        if (imageUri == null){
            saveUserInformation(user, true)
        }else{
            saveUserInformationWithNewImage(user, imageUri)
        }

    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {

            try {
                val imageBitMap = MediaStore.Images.Media.getBitmap(
                    getApplication<KelineApplication>().contentResolver, imageUri )
                val streamOutput = ByteArrayOutputStream()
                imageBitMap.compress(Bitmap.CompressFormat.JPEG, 96, streamOutput)
                val imageByteArray = streamOutput.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()

                saveUserInformation(user.copy(imagePath = imageUrl), false)
            }catch (e: Exception){
                _updateInfo.emit(Resource.Error(e.message.toString()))
            }

        }
    }


    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        //the boolean value allows us to know if we've to keep the old image or update it
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection(USER_COLLECTION).document(auth.uid!!)

            if(shouldRetrieveOldImage){
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef, newUser)
            }else{
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun resetPassword(email: String){
        viewModelScope.launch { _resetPassword.emit(Resource.Loading()) }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch{
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}//USER ACCOUNT VIEW MODEL