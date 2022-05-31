package com.iti.android.team1.ebuy.ui.productsScreen.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti.android.team1.ebuy.databinding.FragmentProductsBinding
import com.iti.android.team1.ebuy.model.datasource.localsource.LocalSource
import com.iti.android.team1.ebuy.model.datasource.localsource.ProductConverter
import com.iti.android.team1.ebuy.model.datasource.repository.Repository
import com.iti.android.team1.ebuy.model.networkresponse.ResultState
import com.iti.android.team1.ebuy.model.pojo.Product
import com.iti.android.team1.ebuy.model.pojo.Products
import com.iti.android.team1.ebuy.ui.productsScreen.ProductsRecyclerAdapter
import com.iti.android.team1.ebuy.ui.productsScreen.viewmodel.ProductViewModelFactory
import com.iti.android.team1.ebuy.ui.productsScreen.viewmodel.ProductsViewModel

private const val TAG = "ProductsFragment"

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private val args: ProductsFragmentArgs by navArgs()
    private val viewModel by viewModels<ProductsViewModel> {
        ProductViewModelFactory(Repository(LocalSource(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProductsById(args.brandId)
        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Loading -> {
                    binding.shimmer.apply {
                        root.showShimmer(true)
                        root.startShimmer()
                    }
                }
                is ResultState.Success -> {
                    binding.apply {
                        shimmer.root.apply {
                            hideShimmer()
                            stopShimmer()
                            visibility = View.GONE
                        }
                        productsRecycler.visibility = View.VISIBLE
                    }
                    setUpProductRecycler(it.data)
                }
                is ResultState.EmptyResult -> {
                    //TODO: Show Empty layout
                    Log.d(TAG, "onViewCreated: EmptyResult")
                }
                is ResultState.Error -> {
                    //TODO: Show Error layout
                    Log.d(TAG, "onViewCreated: Error")

                }
            }
        }
    }

    private var onItemClick: () -> Unit = {
        //TODO: navigate to details_screen
        Log.d(TAG, "onItemClick: called..")
    }

    private var onLike: (Product) -> Unit = { product ->
       viewModel.addProductToFavorite(ProductConverter.convertProductToEntity(product))
    }

    private var onUnLike: (productId:Long) -> Unit = { productId ->
        viewModel.removeProductFromFavorite(productId)
    }

    private fun setUpProductRecycler(products: Products) {
        val productAdapter = ProductsRecyclerAdapter(
            onItemClick = onItemClick,
            onLike = onLike,
            onUnLike = onUnLike
        ).apply {
            setProducts(products)
        }
        binding.productsRecycler.apply {
            layoutManager =
                GridLayoutManager(
                    requireContext(),
                    2,
                    RecyclerView.VERTICAL,
                    false
                )
            adapter = productAdapter
        }

    }
}