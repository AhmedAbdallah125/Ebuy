package com.iti.android.team1.ebuy.ui.register_screen.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.iti.android.team1.ebuy.R
import androidx.navigation.fragment.findNavController
import com.iti.android.team1.ebuy.databinding.FragmentRegisterScreenBinding
import com.iti.android.team1.ebuy.model.datasource.localsource.LocalSource
import com.iti.android.team1.ebuy.model.datasource.repository.Repository
import com.iti.android.team1.ebuy.model.pojo.CustomerRegister
import com.iti.android.team1.ebuy.ui.register_screen.ErrorType
import com.iti.android.team1.ebuy.ui.register_screen.RegisterResult
import com.iti.android.team1.ebuy.ui.register_screen.viewmodel.RegisterViewModel
import com.iti.android.team1.ebuy.ui.register_screen.viewmodel.RegisterViewModelFactory

class RegisterScreen : Fragment() {


    private var _binding: FragmentRegisterScreenBinding? = null


    val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(Repository(LocalSource(requireContext())))
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSignUp.setOnClickListener {
            viewModel.registerCustomer(collectDataFromFields())
        }

        viewModel.registerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterResult.Loading -> showProgressBar()

                is RegisterResult.InvalidData -> {
                    showWrongInputResult(it.error)
                    hideProgressBar()
                }

                is RegisterResult.RegisterFail -> {
                    Toast.makeText(requireContext(),
                        "fail ${it.errorMsg}",
                        Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "onViewCreated:${it.errorMsg} ")
                    hideProgressBar()
                }
                is RegisterResult.RegisterSuccess -> {
                    Toast.makeText(requireContext(),
                        "Welcome ${it.customer.firstName}",
                        Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
            }
        }

    }

    private fun showProgressBar() {
        binding.progress.visibility = View.VISIBLE
        binding.btnSignUp.isEnabled = false
        binding.textInputLayout.isEnabled = false
        binding.textInputLayout2.isEnabled = false
        binding.textInputLayout3.isEnabled = false
        binding.textInputLayout4.isEnabled = false
    }

    private fun hideProgressBar() {
        binding.progress.visibility = View.GONE
        binding.btnSignUp.isEnabled = true
        binding.textInputLayout.isEnabled = true
        binding.textInputLayout2.isEnabled = true
        binding.textInputLayout3.isEnabled = true
        binding.textInputLayout4.isEnabled = true
    }

    private fun collectDataFromFields(): CustomerRegister {
        return CustomerRegister(binding.edtEmail.text.toString().trim(),
            binding.edtFirstName.text.toString().trim(),
            binding.edtLastName.text.toString().trim(),
            binding.edtPassword.text.toString().trim())
    }

    private fun showWrongInputResult(error: ErrorType) {
        when (error) {
            ErrorType.FirstNameError -> binding.edtFirstName.error =
                resources.getString(R.string.name_error)
            ErrorType.LastNameError -> binding.edtLastName.error =
                resources.getString(R.string.name_error)
            ErrorType.EmailError -> binding.edtEmail.error =
                resources.getString(R.string.email_error)
            ErrorType.PasswordError -> binding.edtPassword.error =
                resources.getString(R.string.password_error)

        }
    }

}