package com.example.kelineyt.fragments.shopping

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelineyt.R
import com.example.kelineyt.adapters.SearchAllAdapter
import com.example.kelineyt.databinding.FragmentSearchBinding
import com.example.kelineyt.util.Resource
import com.example.kelineyt.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class SearchFragment: Fragment(){

    private lateinit var binding: FragmentSearchBinding

    private val searchAllAdapter by lazy { SearchAllAdapter() }

    private val searchViewModel by viewModels<SearchViewModel>()

    private val searchTextWatcher = object: TextWatcher{
        private var timer = Timer()
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            timer.cancel()
            timer = Timer()
            timer.schedule(object: TimerTask(){
                override fun run() {
                    searchViewModel.searchProductsByName(p0.toString())
                }
            }, 300L)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchedRv()

        binding.edSearch.addTextChangedListener(searchTextWatcher)

        searchAllAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("producto", it)  }
            findNavController().navigate(R.id.action_searchFragment_to_productsDetailFragment, bundle)
        }

        lifecycleScope.launchWhenStarted {
            searchViewModel.filterProductsByName.collectLatest {
                when(it){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        searchAllAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun initSearchedRv() {
        binding.rvSearchProducts.apply {
            adapter = searchAllAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        }
    }

}