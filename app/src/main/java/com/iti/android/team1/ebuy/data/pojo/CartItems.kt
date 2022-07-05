package com.iti.android.team1.ebuy.data.pojo

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartItems")
data class CartItem(
    @NonNull
    @PrimaryKey
    val productVariantID: Long,
    val productVariantInventoryItemId: Long,
    val productID: Long,
    val productName: String,
    val variantInventoryQuantity: Int,
    val productImageURL: String,
    val productVariantPrice: Double,
    val productVariantOption1: String,
    val productVariantOption2: String,
    var customerProductQuantity: Int = 1,
)
