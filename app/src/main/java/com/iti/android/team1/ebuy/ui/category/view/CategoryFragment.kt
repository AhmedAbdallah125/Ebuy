package com.iti.android.team1.ebuy.ui.category.view

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.FragmentCategoryBinding
import com.iti.android.team1.ebuy.model.datasource.repository.Repository
import com.iti.android.team1.ebuy.model.networkresponse.ResultState
import com.iti.android.team1.ebuy.model.pojo.Categories
import com.iti.android.team1.ebuy.model.pojo.Category
import com.iti.android.team1.ebuy.model.pojo.Products
import com.iti.android.team1.ebuy.ui.category.viewmodel.CategoryViewModel
import com.iti.android.team1.ebuy.ui.category.viewmodel.CategoryViewModelFactory
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import java.text.FieldPosition

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null

    private val categoryViewModel : CategoryViewModel by viewModels {
        CategoryViewModelFactory(Repository())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var categoriesAdapter : CategoriesAdapter
    private lateinit var categoryProductsAdapter: CategoryProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCategoriesRecyclerView()
        initRecyclerView()

        categoryViewModel.getAllCategories()
        categoryViewModel.getAllProduct()

        lifecycleScope.launchWhenStarted {
            categoryViewModel.allCategories.buffer().collect{
                   handleCategoriesResult(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            categoryViewModel.allProducts.buffer().collect{
                handleProductsResult(it)
            }
        }
    }

    private fun handleCategoriesResult(result: ResultState<Categories>){
        when(result){
            is ResultState.Loading->{}
            is ResultState.Success->{
                Log.i("TAG", "handleCategoriesResult: Success")
                result.data.list?.let { categoriesAdapter.setList(it)
                    binding.catTvName.text=it[0].categoryTitle
                }
            }
            is ResultState.EmptyResult->{}
            is ResultState.Error->{}
        }
    }

    private fun handleProductsResult(result: ResultState<Products>){
        when(result){
            is ResultState.Loading->{}
            is ResultState.Success->{
                Log.i("TAG", "handleCategoriesResult: Success")
                result.data.products?.let { categoryProductsAdapter.setList(it) }
            }
            is ResultState.EmptyResult->{}
            is ResultState.Error->{}
        }
    }

    private fun initCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter(emptyList(),::onCategoryClick)
        binding.categoryRecycler.apply {
            this.adapter = categoriesAdapter
            this.layoutManager=LinearLayoutManager(context).apply { this.orientation=RecyclerView.HORIZONTAL }
            this.setHasFixedSize(true)
        }
    }

    private fun onCategoryClick( title : String){
        binding.catTvName.text=title
    }

    private fun initRecyclerView() {
        categoryProductsAdapter = CategoryProductsAdapter(emptyList())
        binding.productRecycler.apply {
            this.adapter = categoryProductsAdapter
            this.layoutManager=GridLayoutManager(context, 2)
            this.setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.category_menu, menu)
        var searchView: SearchView = menu?.findItem(R.id.cat_menu_search)?.actionView as SearchView

        onQueryTextListener(searchView)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun onQueryTextListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                Toast.makeText(context, "" + p0, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                Toast.makeText(context, "" + p0, Toast.LENGTH_SHORT).show()
                return true
            }
        })
    }


}