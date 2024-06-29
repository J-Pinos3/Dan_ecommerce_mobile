package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.CartProduct
import com.example.kelineyt.firebase.FirebaseCommon
import com.example.kelineyt.firebase.FirebaseCommon.QuantityChanging
import com.example.kelineyt.helper.getProductPrice
import com.example.kelineyt.util.Constants.USER_COLLECTION
import com.example.kelineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firesotre: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon

): ViewModel() {

    private val _cartProducts = MutableStateFlow< Resource<List<CartProduct>> >(Resource.Unspecified())
    //val cartProducts = _cartProducts.asSharedFlow() Represents this mutable shared flow as a read-only shared flo
    val cartProducts = _cartProducts.asStateFlow()// Represents this mutable state flow as a read-only state flow.

    private var cartProductsDocuments = emptyList<DocumentSnapshot>()

    private val _DeleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _DeleteDialog.asSharedFlow()

    val productsPrice = cartProducts.map {
        when(it){
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble {
            (it.product.offerPercentage.getProductPrice(it.product.price) * it.quantity).toDouble()
        }.toFloat()

    }

    fun deleteItem(cartProduct: CartProduct){
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if( index != null && index != -1 ){
            val documetId = cartProductsDocuments[index!!].id
            firesotre.collection(USER_COLLECTION).document(auth.uid!!)
                .collection("cart").document(documetId).delete()
        }

    }

    init {
        getCartProducts()
    }

    private fun getCartProducts(){
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
            /**
            * the [addSnapshotListener] is a listener for the collection
            */


            firesotre.collection(USER_COLLECTION)
                .document(auth.uid!!)
                .collection("cart")
                .addSnapshotListener{
                    value, error ->
                    if (error != null || value == null){
                        viewModelScope.launch{
                            _cartProducts.emit(Resource.Error(error?.message.toString()))
                        }

                    }else{
                        cartProductsDocuments = value.documents
                        val cartProducts = value.toObjects(CartProduct::class.java)
                        viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                    }
                }
        }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: QuantityChanging
    ){


        val index = cartProducts.value.data?.indexOf(cartProduct)
        if(index != null && index != -1) {
            val documentId = cartProductsDocuments[index].id
            when(quantityChanging){
                QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }

                QuantityChanging.DECREASE -> {
                    if(cartProduct.quantity == 1){
                        viewModelScope.launch { _DeleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){result, e->
            viewModelScope.launch{
                if (e != null)
                    _cartProducts.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){result, e->
            viewModelScope.launch{
                if (e != null)
                    _cartProducts.emit(Resource.Error(e.message.toString()))
            }
        }
    }

}