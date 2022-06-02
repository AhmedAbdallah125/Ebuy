package com.iti.android.team1.ebuy.model.datasource.remotesource

import com.iti.android.team1.ebuy.model.pojo.*
import retrofit2.Response
import retrofit2.http.*

private const val PASSWORD = "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"

interface RetrofitService {

    @GET("products.json")
    suspend fun getAllProduct(@Header("X-Shopify-Access-Token") pass: String = PASSWORD): Response<Products>

    @GET("products.json")
    suspend fun getProductsByCollectionID(
        @Query("collection_id") collectionID: Long,
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<Products>

    @GET("smart_collections.json")
    suspend fun getAllBrands(
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<Brands>

    @GET("custom_collections.json")
    suspend fun getAllCategories(
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<Categories>

    @GET("products.json")
    suspend fun getAllCategoryProducts(
        @Query("collection_id") collectionID: Long, @Query("product_type") productType: String,
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<Products>

    @GET("products/{product_id}.json")
    suspend fun getProductDetailsById(
        @Path("product_id") product_id: Long,
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<ProductAPI>

    @POST("customers.json")
    suspend fun registerCustomer(
        @Body customerRegister: CustomerRegister,
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<CustomerRegisterAPI>

    @GET("customers/search.json")
    suspend fun loginCustomer(
        customerLogin: CustomerLogin,
        @Query("query") query: String = "tag:${customerLogin.password}+email:${customerLogin.email}",
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ): Response<CustomerLoginAPI>

    @GET("customers/{customer_id}.json")
    suspend fun getCustomerById(
        @Path("customer_id") customer_id: Long,
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
        ): Response<CustomerRegisterAPI>
    @GET("customers/{customer_id}/orders.json")
    suspend fun getCustomerOrders(
        @Path("customer_id") customer_id: Long,
        @Header("X-Shopify-Access-Token") pass: String = PASSWORD,
    ):Response<OrderAPI>
}