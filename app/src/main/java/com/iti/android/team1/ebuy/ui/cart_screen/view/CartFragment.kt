package com.iti.android.team1.ebuy.ui.cart_screen.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.FragmentCartBinding
import com.iti.android.team1.ebuy.model.datasource.localsource.LocalSource
import com.iti.android.team1.ebuy.model.datasource.repository.Repository
import com.iti.android.team1.ebuy.model.networkresponse.ResultState
import com.iti.android.team1.ebuy.ui.cart_screen.adapter.CartProductAdapter
import com.iti.android.team1.ebuy.ui.cart_screen.viewmodel.CartVMFactory
import com.iti.android.team1.ebuy.ui.cart_screen.viewmodel.CartViewModel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val viewModel: CartViewModel by viewModels<CartViewModel> {
        CartVMFactory(Repository(LocalSource(requireContext().applicationContext)))
    }
    private lateinit var cartProductAdapter: CartProductAdapter
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCartRecycler()
        handleAllCartItems()
        handleCheckoutButton()
        handleOverFlow()
        handleDeleteState()
    }

    private fun handleDeleteState() {
        viewModel.deleteState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Error -> Toast.makeText(requireContext(),
                    result.errorString, Toast.LENGTH_SHORT).show()
                ResultState.Loading -> {}
                is ResultState.Success -> {}
            }

        }
    }

    private fun handleEmptyData() {
        binding.cardLayoutSum.visibility = View.INVISIBLE
        binding.recyclerCart.visibility = View.INVISIBLE
        binding.emptyLayout.root.visibility = View.VISIBLE
        binding.emptyLayout.txt.text = getString(R.string.no_item_in_cart)
    }

    private fun handleExistData() {
        binding.cardLayoutSum.visibility = View.VISIBLE
        binding.recyclerCart.visibility = View.VISIBLE
        binding.emptyLayout.root.visibility = View.INVISIBLE
    }


    private fun handleCheckoutButton() {
        binding.btnAddCard.setOnClickListener {
            viewModel.updateToDB()
            // make order object then go to payment Screen
            viewModel.makeOrder()
            lifecycleScope.launchWhenStarted {
                viewModel.order.buffer().collect {
                    val action = CartFragmentDirections.actionCartFragmentToPaymentFragment(it)
                    findNavController().navigate(action)
                }
            }
        }
    }


    private fun handleAllCartItems() {
        viewModel.getAllCartItems()
        lifecycleScope.launch {
            viewModel.allCartItems.observe(viewLifecycleOwner) { result ->
                when (result) {
                    ResultState.EmptyResult -> {
                        handleEmptyData()
                        cartProductAdapter.setCartItems(emptyList())
                    }
                    is ResultState.Error -> Toast.makeText(requireContext(),
                        result.errorString, Toast.LENGTH_SHORT).show()
                    ResultState.Loading -> {
                    }
                    is ResultState.Success -> {
                        handleExistData()
                        cartProductAdapter.setCartItems(result.data)
                        handleCost()
                    }
                }
            }
        }
    }

    private fun handleCost() {
        lifecycleScope.launchWhenStarted {
            viewModel.subTotal.collect { subtotal ->
                viewModel.total.collect { total ->
                    bindCostText(subtotal, total)
                }
            }
        }

    }

    private fun bindCostText(subTotal: Long, total: Long) {
        binding.textProductSubTotal.text = "$subTotal".plus(" EGP")
        binding.textProductLastSubTotal.text =
            "$total".plus(" EGP")
    }

    // function
    private fun initCartRecycler() {
        cartProductAdapter = CartProductAdapter(deleteQuantity, increaseQuantity, decreaseQuantity)
        binding.recyclerCart.apply {
            layoutManager = LinearLayoutManager(
                requireParentFragment().requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            adapter = cartProductAdapter
        }

    }

    private val increaseQuantity: (Int) -> Unit = { it ->
        viewModel.manipulateCartItem(it, CartViewModel.CartItemOperation.INCREASE)
    }

    private fun handleOverFlow() {
        viewModel.isOverFlow.observe(viewLifecycleOwner) { overFlow ->
            if (overFlow) Toast.makeText(requireContext(),
                getString(R.string.no_more_in_stock),
                Toast.LENGTH_SHORT).show()
        }
    }

    private val decreaseQuantity: (Int) -> Unit = {
        viewModel.manipulateCartItem(it, CartViewModel.CartItemOperation.DECREASE)

    }
    private val deleteQuantity: (Int) -> Unit = {
        viewModel.manipulateCartItem(it, CartViewModel.CartItemOperation.DELETE)
    }


    override fun onStop() {
        super.onStop()
        viewModel.updateToDB()
    }
}