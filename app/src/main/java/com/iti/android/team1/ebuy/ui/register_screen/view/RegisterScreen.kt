package com.iti.android.team1.ebuy.ui.register_screen.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.ui.activities.auth.viewmodel.ConnectionViewModel
import com.iti.android.team1.ebuy.ui.activities.main.view.MainActivity
import com.iti.android.team1.ebuy.databinding.FragmentRegisterScreenBinding
import com.iti.android.team1.ebuy.data.datasource.localsource.LocalSource
import com.iti.android.team1.ebuy.data.repository.Repository
import com.iti.android.team1.ebuy.data.pojo.CustomerRegister
import com.iti.android.team1.ebuy.ui.register_screen.AuthResult
import com.iti.android.team1.ebuy.ui.register_screen.ErrorType
import com.iti.android.team1.ebuy.ui.register_screen.viewmodel.RegisterViewModel
import com.iti.android.team1.ebuy.ui.register_screen.viewmodel.RegisterViewModelFactory
import com.iti.android.team1.ebuy.util.showSnackBar
import com.iti.android.team1.ebuy.util.trimText
import kotlinx.coroutines.flow.buffer

class RegisterScreen : Fragment() {

    private var _binding: FragmentRegisterScreenBinding? = null
    private val sharedViewModel by activityViewModels<ConnectionViewModel>()

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(Repository(LocalSource(requireContext())))
    }

    private val binding get() = _binding!!
    var isInternetConnected = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleNoConnection()
        binding.btnSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSignUp.setOnClickListener {
            if (isInternetConnected)
                viewModel.registerCustomer(collectDataFromFields())
            else
                showSnackBar(getString(R.string.not_connected))
        }

        viewModel.registerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is AuthResult.Loading -> showProgressBar()

                is AuthResult.InvalidData -> {
                    showWrongInputResult(it.error)
                    hideProgressBar()
                }

                is AuthResult.RegisterFail -> showSnackBar("fail ${it.errorMsg}")
                is AuthResult.RegisterSuccess -> {
                    hideProgressBar()
                    navigateToMainActivity()
                }
            }
        }

    }

    private fun handleNoConnection() {
        lifecycleScope.launchWhenCreated {
            sharedViewModel.isConnected.buffer().collect { connect ->
                isInternetConnected = connect
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
        return CustomerRegister(binding.edtEmail.trimText(),
            binding.edtFirstName.trimText(),
            binding.edtLastName.trimText(),
            binding.edtPassword.trimText())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToMainActivity() {
        requireContext().startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }
}