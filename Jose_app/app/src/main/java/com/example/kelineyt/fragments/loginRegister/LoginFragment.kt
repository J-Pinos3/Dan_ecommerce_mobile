package com.example.kelineyt.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kelineyt.R
import com.example.kelineyt.activities.ShoppingActivity
import com.example.kelineyt.databinding.FragmentLoginBinding
import com.example.kelineyt.databinding.FragmentRegisterBinding
import com.example.kelineyt.dialog.setupBottomSheetDialog
import com.example.kelineyt.util.Resource
import com.example.kelineyt.util.validateEmail
import com.example.kelineyt.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        binding.apply {
            btnLoginLogin.setOnClickListener {
                val email = etEmailLogin.text.toString().trim()
                val password = etPasswordLogin.text.toString()

                viewModel.login(email, password)
            }
        }


        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect(){
                when(it){
                    is Resource.Loading ->{

                    }

                    is Resource.Success ->{
                        Snackbar.make(requireView(), "Reset Link was sent to email", Snackbar.LENGTH_LONG).show()
                    }

                    is Resource.Error ->{
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit

                }
            }
        }



        lifecycleScope.launchWhenStarted {
            viewModel.login.collect(){
                when(it){
                    is Resource.Loading ->{
                        binding.btnLoginLogin.startAnimation()
                    }

                    is Resource.Success ->{

                        binding.btnLoginLogin.revertAnimation()

                        Intent(requireActivity(), ShoppingActivity::class.java).also {intent ->
                            //this will pop the activity from the stack
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                        binding.btnLoginLogin.revertAnimation()
                    }
                    else -> Unit

                }
            }
        }

    }

}