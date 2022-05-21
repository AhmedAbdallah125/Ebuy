package com.iti.android.team1.ebuy.ui.profile_screen.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti.android.team1.ebuy.databinding.FragmentProfileBinding
import com.iti.android.team1.ebuy.ui.profile_screen.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private var _binding: FragmentProfileBinding? = null
    private lateinit var viewModel: ProfileViewModel
    private val binding get() = _binding!!
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var profileFavoritesAdapter: ProfileFavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOrdersRecyclerView()
        initFavoritesRecyclerView()
    }

    private fun initFavoritesRecyclerView() {
        profileFavoritesAdapter = ProfileFavoritesAdapter()
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false)
            adapter = profileFavoritesAdapter
        }
    }

    private fun initOrdersRecyclerView() {
        ordersAdapter = OrdersAdapter()
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false)
            adapter = ordersAdapter
        }
    }

}