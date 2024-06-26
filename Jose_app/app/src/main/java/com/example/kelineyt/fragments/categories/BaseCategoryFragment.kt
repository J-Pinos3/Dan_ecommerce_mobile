package com.example.kelineyt.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelineyt.R
import com.example.kelineyt.adapters.BestProductsAdapter
import com.example.kelineyt.databinding.FragmentBaseCategoryBinding
import com.example.kelineyt.util.showBottomNavigationView

open class BaseCategoryFragment:Fragment(R.layout.fragment_base_category) {
    // TAB LAYOUT HAS A PROBLEM WHEN REDERING CONTENT DYNAMICALLY
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offersAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOffersRv()
        setUpBestProductsRv()

        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("producto", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailFragment, bundle)
        }

        offersAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("producto", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailFragment, bundle)
        }

        binding.rvOffer.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if( !recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })


        binding.nestesScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
                v, _, scrollY, _,_ ->
            if( v.getChildAt(0).bottom <= v.height + scrollY ){
                onBestProductsPagingRequest()
            }
        })


    }

    fun showOfferLoading(){
        binding.pbOfferProducts.visibility = View.VISIBLE
    }


    fun hideOfferLoading(){
        binding.pbOfferProducts.visibility = View.GONE
    }


    fun showBestProductsLoading(){
        binding.pbBestProducts.visibility = View.VISIBLE
    }


    fun hideBestProductsLoading(){
        binding.pbBestProducts.visibility = View.GONE
    }


    open fun onOfferPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }


    private fun setUpOffersRv(){

        binding.rvOffer .apply {
            adapter = offersAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }


    private fun setUpBestProductsRv(){
        //bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            adapter = bestProductsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }
}