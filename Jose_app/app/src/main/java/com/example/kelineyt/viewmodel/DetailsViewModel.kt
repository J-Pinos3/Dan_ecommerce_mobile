package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.CartProduct
import com.example.kelineyt.firebase.FirebaseCommon
import com.example.kelineyt.util.Constants.USER_COLLECTION
import com.example.kelineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel(){

    private val _addToCart = MutableStateFlow< Resource<CartProduct> >(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()//: Flow<Resource<CartProduct>> = _addToCart


    fun addUpdateProductInCart(cartProduct: CartProduct){

        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION)
            .document(auth.uid!!)
            .collection("cart")
            //for nested document
            .whereEqualTo("product.id",cartProduct.product.id)
            .get()
            .addOnSuccessListener {
                it.documents.let {listOfDocuments->
                    if(listOfDocuments.isEmpty()){
                        addNewProduct(cartProduct)
                    }else{
                        val product = listOfDocuments.first().toObject(CartProduct::class.java)
                        if(product == cartProduct){
                            val documentiD = it.first().id
                            increaseQuantity(documentiD, cartProduct)
                        }else{
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit( Resource.Error(it.message.toString()) )
                }
            }
    }


    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){addedProduct, e ->
            viewModelScope.launch {
                if( e == null ){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId:String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){_, e ->
            viewModelScope.launch {
                if( e == null )
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

}//END OF DETAILS VIEW MODEL