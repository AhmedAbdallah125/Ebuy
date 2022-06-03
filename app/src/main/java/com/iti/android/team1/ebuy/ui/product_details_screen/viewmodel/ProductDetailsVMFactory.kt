package com.iti.android.team1.ebuy.ui.product_details_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iti.android.team1.ebuy.model.datasource.repository.IRepository

class ProductDetailsVMFactory(private val myRepo: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsDetailsViewModel::class.java)) {
            return ProductsDetailsViewModel(myRepo) as T
        } else if (modelClass.isAssignableFrom(AddToCartViewModel::class.java)) {
            return AddToCartViewModel(myRepo) as T
        } else {
            throw IllegalArgumentException("View Model class Not Found")
        }
    }
}