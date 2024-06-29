package com.example.kelineyt.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelineyt.R
import com.example.kelineyt.activities.ShoppingActivity
import com.example.kelineyt.adapters.ColorsAdapter
import com.example.kelineyt.adapters.SizesAdapter
import com.example.kelineyt.adapters.ViewPager2Images
import com.example.kelineyt.data.CartProduct
import com.example.kelineyt.databinding.FragmentProductDetailBinding
import com.example.kelineyt.util.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kelineyt.util.hideBottomNavigationView
import com.example.kelineyt.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductsDetailFragment: Fragment() {
    private val args by navArgs<ProductsDetailFragmentArgs>()

    private lateinit var binding: FragmentProductDetailBinding
    private val colorsAdapter by lazy {  ColorsAdapter() }
    private val sizesAdapter by lazy{ SizesAdapter() }
    private val viewPager2Images by lazy{ ViewPager2Images() }

    private var selectedColor:Int? = null
    private var selectedSize:String? = null
    private val detailsViewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hideBottomNavigationView()
        binding = FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.producto


        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        binding.imClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.cbtnAddToCart.setOnClickListener {
            detailsViewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize) )
        }

        lifecycleScope.launchWhenStarted {
            detailsViewModel.addToCart.collectLatest {
                when(it){

                    is Resource.Loading -> {
                        binding.cbtnAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.cbtnAddToCart.revertAnimation()
                        binding.cbtnAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }

                    is Resource.Error -> {
                        binding.cbtnAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            if( product.colors.isNullOrEmpty() )
                tvProductColors.visibility = View.INVISIBLE

            if( product.sizes.isNullOrEmpty() )
                tvProductSizes.visibility = View.INVISIBLE
        }

        viewPager2Images.differ.submitList(product.images)
        //if colors or sizes is not empty, let's body will be executed
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }

        product.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }
    }


    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupViewPager() {
        binding.apply {
            vpProductImages.adapter = viewPager2Images
        }
    }
}