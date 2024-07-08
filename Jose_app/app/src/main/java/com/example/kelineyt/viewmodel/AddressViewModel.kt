package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.Address
import com.example.kelineyt.util.Constants.ADDRESS_COLLECTION
import com.example.kelineyt.util.Constants.CART_COLLECTION
import com.example.kelineyt.util.Constants.USER_COLLECTION
import com.example.kelineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):  ViewModel() {

    private val _addNewAddress = MutableStateFlow< Resource<Address> >( Resource.Unspecified() )
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _Error = MutableSharedFlow<String>()
    val errot = _Error.asSharedFlow()

    fun addAddress(address: Address){
        val validInputs: Boolean = validateInputs(address)
        if(validInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Unspecified()) }
            firestore.collection(USER_COLLECTION)
                .document(auth.uid!!)
                .collection(ADDRESS_COLLECTION)
                .document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _addNewAddress.emit(Resource.Success(address))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _addNewAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
        else{
            viewModelScope.launch{
                _Error.emit("All fields are mandatory")
            }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.isNotEmpty() && address.city.isNotEmpty()
                && address.phone.isNotEmpty() && address.fullName.isNotEmpty()
                && address.state.isNotEmpty() && address.street.isNotEmpty()
    }


}