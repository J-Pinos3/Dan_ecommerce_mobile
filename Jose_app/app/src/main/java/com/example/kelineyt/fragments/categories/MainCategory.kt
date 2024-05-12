package com.example.kelineyt.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelineyt.R
import com.example.kelineyt.adapters.BestDealsProductsAdapter
import com.example.kelineyt.adapters.SpecialProductsAdapter
import com.example.kelineyt.data.Product
import com.example.kelineyt.databinding.FragmentMainCategoryBinding
import com.example.kelineyt.util.Resource
import com.example.kelineyt.viewmodel.MainCategoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainCategory:Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsProductsAdapter: BestDealsProductsAdapter

    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsProductsRv()

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest{
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }

                    is Resource.Success ->{
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Error ->{
                        hideLoading()
                        Log.e("MainCategoryFragment","Error: ${it.message}")
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit//emptyList<Product>()
                }
            }//special products viewmodel

        }


        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        bestDealsProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e("MainCategoryFragment","Error: ${it.message}")
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }//best deals products view holder

        }




    }



    private fun hideLoading() {
        binding.pbMainCategory.visibility = View.GONE
    }

    private fun showLoading() {
        binding.pbMainCategory.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            adapter = specialProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupBestDealsProductsRv(){
        bestDealsProductsAdapter = BestDealsProductsAdapter()
        binding.rvBestDealProducts.apply {
            adapter = bestDealsProductsAdapter
            layoutManager  = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}