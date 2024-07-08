package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.Address
import com.example.kelineyt.util.Constants.ADDRESS_COLLECTION
import com.example.kelineyt.util.Constants.USER_COLLECTION

import com.example.kelineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    //retrieve addresses from firestore
    private val _address = MutableStateFlow< Resource<List<Address>> >( Resource.Unspecified() )
    val address = _address.asStateFlow()

    init {
        getUserAddresses()
    }

    fun getUserAddresses(){
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION)
            .document(auth.uid!!)
            .collection(ADDRESS_COLLECTION)
            .addSnapshotListener {
                value, error ->

                if(error != null || value == null){
                    viewModelScope.launch {
                        _address.emit(Resource.Error(error?.message.toString()))
                    }
                    return@addSnapshotListener
                }else{

                    val addressObjects = value.toObjects(Address::class.java)
                    viewModelScope.launch { _address.emit(Resource.Success(addressObjects)) }
                }
            }

    }

}