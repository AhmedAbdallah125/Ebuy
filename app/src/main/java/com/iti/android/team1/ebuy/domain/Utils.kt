package com.iti.android.team1.ebuy.domain

import com.iti.android.team1.ebuy.data.factories.NetworkResponse
import com.iti.android.team1.ebuy.data.pojo.DraftsLineItems
import com.iti.android.team1.ebuy.data.pojo.Products

fun productsResponse(
    networkResponse: NetworkResponse<Products>,
    allFavoritesProducts: List<DraftsLineItems>,
): NetworkResponse<Products> {
    when (networkResponse) {
        is NetworkResponse.FailureResponse -> return networkResponse
        is NetworkResponse.SuccessResponse -> {
            val productMap = networkResponse.data.products?.associateBy {
                it.productID
            }

            allFavoritesProducts.forEach { favoriteProduct ->
                productMap?.get(favoriteProduct.productId)?.apply {
                    isFavorite = true
                }
            }

            return NetworkResponse.SuccessResponse(Products(productMap?.values?.toList()
                ?: emptyList()))
        }
    }
}

