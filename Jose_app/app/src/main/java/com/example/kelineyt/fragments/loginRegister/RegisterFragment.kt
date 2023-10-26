package com.example.kelineyt.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kelineyt.R
import com.example.kelineyt.data.User
import com.example.kelineyt.databinding.FragmentRegisterBinding
import com.example.kelineyt.util.Resource
import com.example.kelineyt.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private  val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegisterRegister.setOnClickListener {
                val user = User(
                    etNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim()
                )

                val password = etPasswordRegister.text.toString()
                viewModel.createAccountWithEmailandPassword(user, password)
            }

        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnRegisterRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d( "Test",it.data.toString() )
                        binding.btnRegisterRegister.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.e( TAG,it.message.toString() )
                        binding.btnRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

    }//ON VIEW CREATED


}