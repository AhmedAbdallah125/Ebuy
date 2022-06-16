package com.iti.android.team1.ebuy.domain.savedItems

import com.iti.android.team1.ebuy.model.factories.NetworkResponse
import com.iti.android.team1.ebuy.model.pojo.Product

interface ISavedItemsUseCase {
    suspend fun getFavoriteItems(): NetworkResponse<List<Product>>
}