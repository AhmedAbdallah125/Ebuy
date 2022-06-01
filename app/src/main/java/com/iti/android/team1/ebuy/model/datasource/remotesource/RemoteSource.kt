package com.iti.android.team1.ebuy.model.datasource.remotesource

import retrofit2.Response
import com.iti.android.team1.ebuy.model.pojo.*

interface RemoteSource {
    suspend fun getProductsByCollectionID(
        collectionID: Long,
    ): Response<Products>

    suspend fun getAllBrands(): Response<Brands>
    suspend fun getAllProduct(): Response<Products>
    suspend fun getAllCategories(): Response<Categories>
    suspend fun getAllCategoryProducts(collectionID: Long, productType: String): Response<Products>
    suspend fun getProductDetails(product_id: Long): Response<ProductAPI>
    suspend fun createCustomer(customerRegister: CustomerRegister): Response<CustomerRegisterAPI>
    suspend fun getCustomer(customerLogin: CustomerLogin): Response<CustomerLoginAPI>
}