package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.Product
import com.example.kelineyt.util.Constants.PRODUCTS_COLLECTION
import com.example.kelineyt.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor (
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow< Resource<List<Product>> >(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow< Resource<List<Product>> >(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    init {
        fetchSpecialProducts()
        fetchBestDeals()
    }

    fun fetchSpecialProducts(){
        viewModelScope.launch {
            //runBlocking {
            //runBlocking is similar to .join() in c++ or java, it blocks the main thread
            //till this new thread finish its job
            _specialProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo( "category", "Special Products" ).get()
            .addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch{
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }

            }
            .addOnFailureListener {
                viewModelScope.launch{
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }



    fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("category", "Best Deals").get()
            .addOnSuccessListener { result ->
                val bestDealsProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProductsList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}