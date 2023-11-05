package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kelineyt.data.User
import com.example.kelineyt.util.RegisterFieldState
import com.example.kelineyt.util.RegisterValidation
import com.example.kelineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import com.example.kelineyt.util.validateEmail
import com.example.kelineyt.util.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel

class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {


    private val _register = MutableStateFlow< Resource<FirebaseUser> >(Resource.Unspecified())
    public val register: Flow<Resource<FirebaseUser>> = _register

    private val _validation =   Channel<RegisterFieldState> ()
    val validation =   _validation.receiveAsFlow()

    fun createAccountWithEmailandPassword(user: User, password:String){

        if(checkValidation(user, password)){
            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        _register.value = Resource.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }

        }else{
            val registerFieldState = RegisterFieldState( validateEmail(user.email), validatePassword(password) )
            runBlocking {
                _validation.send(registerFieldState)
            }

        }

    }


    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }


}