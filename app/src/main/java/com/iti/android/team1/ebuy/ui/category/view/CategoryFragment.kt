package com.iti.android.team1.ebuy.ui.category.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.FragmentCategoryBinding
import com.iti.android.team1.ebuy.model.pojo.Category
import com.iti.android.team1.ebuy.ui.category.viewmodel.CategoryViewModel

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoryViewModel =
            ViewModelProvider(this).get(CategoryViewModel::class.java)

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

       // initCategoriesRecyclerView()
       // initRecyclerView()

        return binding.root
    }

    private fun initCategoriesRecyclerView(categories : List<Category>) {
        binding.categoryRecycler.apply {
            this.adapter = CategoriesAdapter(categories)
            this.layoutManager=LinearLayoutManager(context).apply { this.orientation=RecyclerView.HORIZONTAL }
            this.setHasFixedSize(true)
        }
    }

    private fun initRecyclerView() {
        binding.productRecycler.apply {
            this.adapter = CategoryProductsAdapter()
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