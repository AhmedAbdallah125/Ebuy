package com.iti.android.team1.ebuy.ui.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.FragmentHomeBinding
import com.iti.android.team1.ebuy.data.data.localsource.LocalSource
import com.iti.android.team1.ebuy.data.data.repository.Repository
import com.iti.android.team1.ebuy.data.factories.ResultState
import com.iti.android.team1.ebuy.data.pojo.Brands
import com.iti.android.team1.ebuy.data.pojo.DiscountCodes
import com.iti.android.team1.ebuy.ui.home.adapters.HomeRecyclerAdapter
import com.iti.android.team1.ebuy.ui.home.adapters.HomeViewPagerAdapter
import com.iti.android.team1.ebuy.ui.home.viewmodel.HomeViewModel
import com.iti.android.team1.ebuy.ui.home.viewmodel.HomeViewModelFactory
import com.iti.android.team1.ebuy.util.ZoomOutPageTransformer
import com.iti.android.team1.ebuy.util.showSnackBar
import kotlinx.coroutines.flow.buffer

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(Repository(LocalSource(requireContext())))
    }
    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!
    private var _brandsAdapter: HomeRecyclerAdapter? = null
    private val brandsAdapter get() = _brandsAdapter!!
    private var _discountAdapter: HomeViewPagerAdapter? = null
    private val discountAdapter get() = _discountAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initAdsViewPager()
        initBrandsRecyclerView()
        handleDiscountCodes()
        homeViewModel.getAllBrands()
        lifecycleScope.launchWhenStarted {
            homeViewModel.brandsResult.buffer().collect { brandsResult ->
                handleResultStates(brandsResult)
            }
        }
    }

    private fun handleDiscountCodes() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getAllDiscount()
            homeViewModel.discountCodeList.buffer().collect { result ->
                when (result) {
                    ResultState.EmptyResult -> discountAdapter.discountCodeList = emptyList()
//                    is ResultState.Error -> TODO()
//                    ResultState.Loading -> TODO()
                    is ResultState.Success -> {
                        discountAdapter.discountCodeList = result.data
                    }
                }
            }
        }
    }

    private fun handleResultStates(brandsResult: ResultState<Brands>) {
        when (brandsResult) {
            ResultState.EmptyResult -> {
                hideShimmer()
                binding.homeRecyclerview.visibility = View.GONE
                binding.emptyLayout.root.visibility = View.VISIBLE
                binding.emptyLayout.txt.text = getString(R.string.no_brands_found)
            }
            is ResultState.Error -> {
                hideShimmer()
                showSnackBar(brandsResult.errorString)
            }
            ResultState.Loading -> {
                showShimmer()
            }
            is ResultState.Success -> {
                hideShimmer()
                binding.homeRecyclerview.visibility = View.VISIBLE
                binding.emptyLayout.root.visibility = View.GONE
                brandsAdapter.setBrandsList(brandsResult.data)
            }
        }
    }

    private fun initBrandsRecyclerView() {
        _brandsAdapter = HomeRecyclerAdapter(
            onBrandClick
        )
        binding.homeRecyclerview.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.VERTICAL,
                false
            )
            adapter = brandsAdapter
        }
    }

    private fun initAdsViewPager() {
        _discountAdapter = HomeViewPagerAdapter(showDiscountDialog)
        binding.viewPager2.adapter = discountAdapter
        binding.wormDotsIndicator.attachTo(binding.viewPager2)
        val zoomOutPageTransformer = ZoomOutPageTransformer()
        binding.viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboardManager =
            requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        showSnackBar(getString(R.string.copied_code))
    }

    private val showDiscountDialog: (DiscountCodes) -> Unit = {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(getString(R.string.discount))
            .setMessage("${it.code}")
            .setNegativeButton(getString(R.string.copy_code)) { dialog, which ->
                copyTextToClipboard(it.code ?: "0")
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                // Respond to positive button press
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.queryHint = getString(R.string.brands_search)
        onQueryTextListener(searchView)
        onCloseSearch(searchView)
    }

    private fun onCloseSearch(searchView: SearchView) {
        searchView.setOnCloseListener {
            homeViewModel.getBrandsAgain()
            return@setOnCloseListener false
        }
    }

    private fun onQueryTextListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    homeViewModel.setSearchQuery(it.trim())
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    homeViewModel.setSearchQuery(query.trim())
                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToSettingsFragment())
            R.id.action_about -> {//TODO: Navigate to about screen
            }
            R.id.action_favorite -> findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationFavorites())
        }
        return super.onOptionsItemSelected(item)
    }

    private var onBrandClick: (Long, String) -> Unit = { collectionID, brandTitle ->
        findNavController().navigate(
            HomeFragmentDirections.actionNavigationHomeToNavigationProducts(
                brandTitle,
                collectionID
            )
        )
    }

    private fun showShimmer() {
        binding.brandsShimmer.root.apply {
            showShimmer(true)
            startShimmer()
        }
    }

    private fun hideShimmer() {
        binding.brandsShimmer.root.apply {
            hideShimmer()
            stopShimmer()
            visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.onDestroyView()
        _brandsAdapter = null
        _discountAdapter = null
        _binding = null
    }
}