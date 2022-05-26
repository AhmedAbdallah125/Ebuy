package com.iti.android.team1.ebuy.ui.home.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.FragmentHomeBinding
import com.iti.android.team1.ebuy.model.datasource.repository.Repository
import com.iti.android.team1.ebuy.model.networkresponse.ResultState
import com.iti.android.team1.ebuy.model.pojo.Brands
import com.iti.android.team1.ebuy.ui.home.adapters.HomeRecyclerAdapter
import com.iti.android.team1.ebuy.ui.home.adapters.HomeViewPagerAdapter
import com.iti.android.team1.ebuy.ui.home.viewmodel.HomeViewModel
import com.iti.android.team1.ebuy.ui.home.viewmodel.HomeViewModelFactory
import com.iti.android.team1.ebuy.util.ZoomOutPageTransformer
import kotlinx.coroutines.flow.buffer

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(Repository())
    }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var brandsAdapter: HomeRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initAdsViewPager()
        initBrandsRecyclerView()
        homeViewModel.getAllBrands()
        lifecycleScope.launchWhenStarted {
            homeViewModel.brandsResult.buffer().collect { brandsResult ->
                handleResultStates(brandsResult)
            }
        }
    }

    private fun handleResultStates(brandsResult: ResultState<Brands>) {
        when (brandsResult) {
            ResultState.EmptyResult -> {
                //TODO show empty result
            }
            is ResultState.Error -> {
                binding.brandsShimmer.visibility = View.GONE
                binding.brandsShimmer.stopShimmer()
                Toast.makeText(requireContext(), brandsResult.errorString, Toast.LENGTH_LONG).show()
            }
            ResultState.Loading -> {
                binding.brandsShimmer.startShimmer()
            }
            is ResultState.Success -> {
                binding.brandsShimmer.visibility = View.GONE
                binding.brandsShimmer.stopShimmer()
                brandsAdapter.setBrandsList(brandsResult.data)
            }
        }
    }

    private fun initBrandsRecyclerView() {
        brandsAdapter = HomeRecyclerAdapter(
            requireContext(),
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
        val adapter = HomeViewPagerAdapter()
        binding.viewPager2.adapter = adapter
        binding.wormDotsIndicator.attachTo(binding.viewPager2)
        val zoomOutPageTransformer = ZoomOutPageTransformer()
        binding.viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {//TODO: Navigate to settings screen
            }
            R.id.action_about -> {//TODO: Navigate to about screen
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}