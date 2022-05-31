package com.iti.android.team1.ebuy.model.datasource.remotesource

import com.iti.android.team1.ebuy.model.pojo.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://mobile-ismailia.myshopify.com/admin/api/2022-04/"


private val retrofit = Retrofit.Builder().apply {
    addConverterFactory(GsonConverterFactory.create())
    baseUrl(BASE_URL)
}.build()

object RetrofitHelper : RemoteSource {
    private val retrofitService by lazy { retrofit.create(RetrofitService::class.java) }

    override suspend fun getProductsByCollectionID(collectionID: Long): Response<Products> {
        return retrofitService.getProductsByCollectionID(collectionID)
    }

    override suspend fun getAllBrands(): Response<Brands> {
        return retrofitService.getAllBrands()
    }

    override suspend fun getAllProduct(): Response<Products> {
        return retrofitService.getAllProduct()
    }

    override suspend fun getAllCategories(): Response<Categories> {
        return retrofitService.getAllCategories()
    }

    override suspend fun getAllCategoryProducts(
        collectionID: Long,
        productType: String,
    ): Response<Products> {
        return retrofitService.getAllCategoryProducts(collectionID, productType)
    }

    override suspend fun getProductDetails(product_id: Long): Response<ProductAPI> {
        return retrofitService.getProductDetailsById(product_id)
    }

    override suspend fun createCustomer(customerRegister: CustomerRegister):Response<CustomerRegisterAPI> {
        return retrofitService.createCustomer(customerRegister)
    }

    override suspend fun getCustomer(customerLogin: CustomerLogin): Response<CustomerLoginAPI> {
        return retrofitService.getCustomer(customerLogin)
    }
}