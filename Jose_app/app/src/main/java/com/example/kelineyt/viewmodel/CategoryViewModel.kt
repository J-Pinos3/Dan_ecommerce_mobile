package com.example.kelineyt.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.Category
import com.example.kelineyt.data.Product
import com.example.kelineyt.util.Constants.PRODUCTS_COLLECTION
import com.example.kelineyt.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,  private val category: Category
):ViewModel(){

    private val _offerProducts = MutableStateFlow< Resource<List<Product>> >( Resource.Unspecified() )
    val offerProducts: StateFlow< Resource<List<Product>> > = _offerProducts

    private val _bestProducts = MutableStateFlow< Resource<List<Product>> >( Resource.Unspecified() )
    val bestProducts = _bestProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }


    fun fetchOfferProducts(){

        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener {querySnapshot ->
                val offerProductsList = querySnapshot.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit( Resource.Success(offerProductsList) )
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit( Resource.Error(it.message.toString()) )
                }
            }
    }


    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("category",category.category)
            .whereEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener {querySnapshot ->
                val bestProductsList = querySnapshot.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit( Resource.Success(bestProductsList) )
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit( Resource.Error(it.message.toString()) )
                }
            }
    }


}