package com.iti.android.team1.ebuy.ui.savedItems.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.FragmentSavedItemsBinding
import com.iti.android.team1.ebuy.model.DatabaseResult
import com.iti.android.team1.ebuy.model.datasource.localsource.LocalSource
import com.iti.android.team1.ebuy.model.datasource.repository.Repository
import com.iti.android.team1.ebuy.model.pojo.FavoriteProduct
import com.iti.android.team1.ebuy.ui.savedItems.adapter.SavedRecyclerAdapter
import com.iti.android.team1.ebuy.ui.savedItems.viewmodel.SavedItemsViewModel
import com.iti.android.team1.ebuy.ui.savedItems.viewmodel.SavedItemsViewModelFactory
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.buffer

private const val TAG = "SavedItemsFragment"

class SavedItemsFragment : Fragment() {

    private lateinit var binding: FragmentSavedItemsBinding
    private lateinit var favoritesAdapter: SavedRecyclerAdapter
    private lateinit var previousData: FavoriteProduct
    private var lastUpdatedPosition: Int? = null

    private val viewModel: SavedItemsViewModel by viewModels {
        SavedItemsViewModelFactory(
            Repository(
                LocalSource(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSavedItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoritesList()
        fetchFavoritesData()
    }

    private fun fetchFavoritesData() {
        lifecycleScope.launchWhenStarted {
            viewModel.allFavorites.buffer().collect { response ->
                when (response) {
                    DatabaseResult.Empty -> {

                        binding.emptyLayout.root.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE

                        binding.emptyLayout.txt.text = getString(R.string.favorite_empty_list_title)
                    }
                    is DatabaseResult.Success -> {
                        binding.emptyLayout.root.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE

                        favoritesAdapter = SavedRecyclerAdapter(
                            onItemClick, onIncreaseClick, onDecreaseClick
                        )

                        favoritesAdapter.setAdapterList(response.data)

                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = favoritesAdapter
                        }

                    }
                    else -> Log.d(TAG, "fetchFavoritesData: unexpected callback !!")
                }
            }
        }

    }

    private var onItemClick: (Long) -> Unit = { productId ->
        findNavController().navigate(
            SavedItemsFragmentDirections.actionNavigationFavoritesToProductsDetailsFragment(
                productId)
        )
    }

    private var onIncreaseClick: (FavoriteProduct, Int) -> Unit = { favoriteProduct, position ->
        lastUpdatedPosition = position
        previousData = favoriteProduct
        favoriteProduct.noOfSavedItems++
        viewModel.updateFavoriteProduct(favoriteProduct)
        fetchUpdateState(position)
    }

    private var onDecreaseClick: (FavoriteProduct, Int) -> Unit = { favoriteProduct, position ->
        lastUpdatedPosition = position
        previousData = favoriteProduct
        favoriteProduct.noOfSavedItems--
        viewModel.updateFavoriteProduct(favoriteProduct)
        fetchUpdateState(position)
    }

    private fun fetchUpdateState(position: Int) {
        lifecycleScope.launchWhenStarted {
            viewModel.updateState.buffer().collect { response ->
                when (response) {
                    DatabaseResult.Empty -> {
                        favoritesAdapter.notifyItemChanged(position)
                    }
                    is DatabaseResult.Error -> {
                        Toast.makeText(requireContext(),
                            response.errorMsg,
                            Toast.LENGTH_SHORT).show()
                        favoritesAdapter.updateItemAtIndex(lastUpdatedPosition ?: -1,
                            previousData)
                    }
                    else -> Log.d(TAG, "fetchFavoritesData: unexpected callback !!")
                }
                cancel()
            }
        }
    }

}