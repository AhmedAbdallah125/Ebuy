package com.iti.android.team1.ebuy.ui.productsScreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.android.team1.ebuy.domain.brandproducts.BrandProductsUseCase
import com.iti.android.team1.ebuy.domain.brandproducts.IBrandProductsUseCase
import com.iti.android.team1.ebuy.model.datasource.repository.IRepository
import com.iti.android.team1.ebuy.model.networkresponse.NetworkResponse
import com.iti.android.team1.ebuy.model.networkresponse.ResultState
import com.iti.android.team1.ebuy.model.pojo.Products
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProductsViewModel(private val repoInterface: IRepository) : ViewModel() {

    private val _productsMutableLivaData: MutableLiveData<ResultState<Products>> = MutableLiveData()

    val productsLiveData: LiveData<ResultState<Products>>
        get() = _productsMutableLivaData

    private val brandProductsUseCase: IBrandProductsUseCase
        get() = BrandProductsUseCase(repoInterface)

    fun getProductsById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = async {
                brandProductsUseCase.getProductsByCollectionID(id)
            }
            fetchResult(res.await())
        }
    }

    private fun fetchResult(networkResponse: NetworkResponse<Products>) {
        _productsMutableLivaData.postValue(ResultState.Loading)
        when (networkResponse) {
            is NetworkResponse.SuccessResponse -> {
                networkResponse.data.let {
                    if (!it.products.isNullOrEmpty()) {
                        _productsMutableLivaData.postValue(ResultState.Success(it))
                    } else {
                        _productsMutableLivaData.postValue(ResultState.EmptyResult)
                    }
                }
            }
            is NetworkResponse.FailureResponse -> {
                _productsMutableLivaData.postValue(ResultState.Error(networkResponse.errorString))
            }
        }
    }
}