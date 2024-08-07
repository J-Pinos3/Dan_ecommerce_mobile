package com.example.kelineyt.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelineyt.R
import com.example.kelineyt.adapters.CartProductsAdapter
import com.example.kelineyt.databinding.FragmentCartBinding
import com.example.kelineyt.firebase.FirebaseCommon
import com.example.kelineyt.util.Resource
import com.example.kelineyt.util.VerticalItemDecoration
import com.example.kelineyt.viewmodel.CartViewModel
import dagger.hilt.android.components.ViewWithFragmentComponent
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(){
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductsAdapter() }
    //private val cartViewModel by viewModels<CartViewModel> ()this way, It triggers the getCartProducts twice
    //we already get the cart within the shopping activity
    private val cartViewModel by activityViewModels<CartViewModel> ()//triggers the getCartProducts once


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            cartViewModel.productsPrice.collectLatest {price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productsDetailFragment, b)
        }

        cartAdapter.onPlusClick = {
            cartViewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            cartViewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener {


            val action = CartFragmentDirections.actionCartFragmentToBillingFragment2(totalPrice, cartAdapter.differ.currentList.toTypedArray(), true)
            findNavController().navigate(action)
            //findNavController().navigate(R.id.action_cartFragment_to_billingFragment2)
        }


        lifecycleScope.launchWhenStarted {
            cartViewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item?")
                    setNegativeButton("Cancel"){dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Continue"){dialog,_ ->
                        cartViewModel.deleteItem(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }

        }

        lifecycleScope.launchWhenStarted {
            cartViewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressBarCart.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBarCart.visibility = View.INVISIBLE
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }else{
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList((it.data))
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.VISIBLE
        }
    }


    private fun setupCartRv() {
        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}