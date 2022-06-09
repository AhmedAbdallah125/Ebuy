package com.iti.android.team1.ebuy.ui.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.android.team1.ebuy.domain.category.CategoryProductsUseCase
import com.iti.android.team1.ebuy.domain.category.ICategoryProductsUseCase
import com.iti.android.team1.ebuy.model.datasource.repository.IRepository
import com.iti.android.team1.ebuy.model.networkresponse.NetworkResponse
import com.iti.android.team1.ebuy.model.networkresponse.ResultState
import com.iti.android.team1.ebuy.model.pojo.Categories
import com.iti.android.team1.ebuy.model.pojo.DraftOrder
import com.iti.android.team1.ebuy.model.pojo.Product
import com.iti.android.team1.ebuy.model.pojo.Products
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private var myRepo: IRepository) : ViewModel() {
    private var _allProducts = MutableStateFlow<ResultState<Products>>(ResultState.Loading)
    val allProducts get() = _allProducts.asStateFlow()
    private var _allCategories = MutableStateFlow<ResultState<Categories>>(ResultState.Loading)
    val allCategories get() = _allCategories.asStateFlow()

    private var _insertFavoriteProductToDataBase =
        MutableStateFlow<ResultState<DraftOrder>>(ResultState.Loading)
    val insertFavoriteProductToDataBase
        get() = _insertFavoriteProductToDataBase.asStateFlow()

    private var _deleteFavoriteProductToDataBase =
        MutableStateFlow<ResultState<DraftOrder>>(ResultState.Loading)
    val deleteFavoriteProductToDataBase
        get() = _deleteFavoriteProductToDataBase.asStateFlow()

    private val categoryProductsUseCase: ICategoryProductsUseCase
        get() = CategoryProductsUseCase(myRepo)

    private var cachedProducts: List<Product>? = null

    fun getAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = async {
                categoryProductsUseCase.getAllCategories()
            }
            sendCategoriesResponse(result.await())
        }
    }

    fun getAllProduct(category: Long = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            _allProducts.emit(ResultState.Loading)
            val result = async {
                if (category == 0L) categoryProductsUseCase.getAllProducts()
                else categoryProductsUseCase.getProductsByCollectionID(category)
            }
            sendProductsByTypeResponse(result.await())
        }
    }

    private suspend fun sendCategoriesResponse(result: NetworkResponse<Categories>) {
        when (result) {
            is NetworkResponse.FailureResponse ->
                _allCategories.emit(ResultState.Error(result.errorString))
            is NetworkResponse.SuccessResponse -> {
                if (result.data.categoriesList.isEmpty()) {
                    _allCategories.emit(ResultState.EmptyResult)
                } else {
                    _allCategories.emit(ResultState.Success(result.data))
                }
            }
        }
    }

    //home is default category
    fun getAllProductByType(categoryId: Long = 395727569125, productType: String = "SHOES") {
        viewModelScope.launch(Dispatchers.IO) {
            _allProducts.emit(ResultState.Loading)
            val result = async {
                categoryProductsUseCase.getAllCategoryProductsByType(categoryId, productType)
            }
            sendProductsByTypeResponse(result.await())
        }
    }

    private suspend fun sendProductsByTypeResponse(result: NetworkResponse<Products>) {
        when (result) {
            is NetworkResponse.FailureResponse ->
                _allProducts.emit(ResultState.Error(result.errorString))
            is NetworkResponse.SuccessResponse -> {
                if (result.data.products.isNullOrEmpty()) {
                    _allProducts.emit(ResultState.EmptyResult)
                } else
                    cachedProducts = result.data.products
                _allProducts.emit(ResultState.Success(result.data))
            }
        }
    }

    fun addProductToFavorite(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = async {
                myRepo.addFavorite(product)
            }
            addFavoriteResponse(result.await())
        }
    }

    private fun addFavoriteResponse(response: NetworkResponse<DraftOrder>) {
        when (response) {
            is NetworkResponse.FailureResponse -> _insertFavoriteProductToDataBase.value =
                ResultState.Error(response.errorString)
            is NetworkResponse.SuccessResponse -> {
                _insertFavoriteProductToDataBase.value = ResultState.Success(response.data)
            }
        }
    }

    fun removeFavoriteProduct(productID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = async {
                myRepo.removeFromFavorite(productID)
            }
            removeFavoriteResponse(result.await())
        }
    }

    private fun removeFavoriteResponse(response: NetworkResponse<DraftOrder>) {
        when (response) {
            is NetworkResponse.FailureResponse -> _deleteFavoriteProductToDataBase.value =
                ResultState.Error(response.errorString)
            is NetworkResponse.SuccessResponse -> {
                _deleteFavoriteProductToDataBase.value = ResultState.Success(response.data)
            }
        }
    }

    fun setSearchQuery(newString: String) {
        _allProducts.value = ResultState.Loading
        val searchList: List<Product> = cachedProducts ?: emptyList()
        viewModelScope.launch(Dispatchers.Default) {
            if (searchList.isNotEmpty()) {
                searchList.filter { product ->
                    filterProduct(product, newString)
                }.apply {
                    if (this.isNotEmpty())
                        _allProducts.emit(ResultState.Success(Products(this)))
                    else
                        _allProducts.emit(ResultState.EmptyResult)
                }
            }
        }
    }

    private fun filterProduct(product: Product, newString: String): Boolean {
        return product.productName?.lowercase()?.contains(newString.lowercase()) == true ||
                product.productVendor?.lowercase()?.contains(newString.lowercase()) == true
    }

    fun getProductsAgain() {
        cachedProducts?.let {
            _allProducts.value = ResultState.Success(Products(it))
        }
    }
}

