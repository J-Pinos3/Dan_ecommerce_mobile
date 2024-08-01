package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kelineyt.data.Product
import com.example.kelineyt.util.Constants.PRODUCTS_COLLECTION
import com.example.kelineyt.util.Resource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel(){

    private val _productsByName = MutableStateFlow< Resource<List<Product>> >( Resource.Unspecified() )
    val filterProductsByName = _productsByName.asStateFlow()

    fun searchProductsByName(mealNameFilter: String){

        viewModelScope.launch {  _productsByName.emit(Resource.Loading())  }
        firestore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("name", mealNameFilter)
            .get()
            .addOnSuccessListener {
                val searchedProductsList = it.toObjects(Product::class.java)
                viewModelScope.launch{
                    _productsByName.emit( Resource.Success(searchedProductsList) )
                }
            }
            .addOnFailureListener {
                viewModelScope.launch{
                    _productsByName.emit( Resource.Error(it.message.toString()) )
                }
            }

    }

}