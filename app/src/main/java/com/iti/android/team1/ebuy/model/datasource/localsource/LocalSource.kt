package com.iti.android.team1.ebuy.model.datasource.localsource

import android.content.Context
import com.iti.android.team1.ebuy.model.datasource.localsource.dao.CartDao
import com.iti.android.team1.ebuy.model.datasource.localsource.dao.FavoritesDao
import com.iti.android.team1.ebuy.model.datasource.localsource.prefs.PreferenceProvider
import com.iti.android.team1.ebuy.model.pojo.CartItem
import com.iti.android.team1.ebuy.model.pojo.FavoriteProduct
import kotlinx.coroutines.flow.Flow

class LocalSource(
    private val context: Context,
    private val favoritesDao: FavoritesDao = CommerceDatabase.getDataBase(context).favoritesDao(),
    private val cartDao: CartDao = CommerceDatabase.getDataBase(context).cartDao(),
    private val prefs: PreferenceProvider = PreferenceProvider.getInstance(context),
) : ILocalSource {

    override suspend fun addProductToFavorites(favoriteProduct: FavoriteProduct): Long {
        return favoritesDao.insertProductToFavorite(favoriteProduct)
    }

    override suspend fun removeProductFromFavorites(productID: Long): Int {
        return favoritesDao.deleteProductFromFavorite(productID)
    }

    override suspend fun getAllFavoriteProducts(): List<FavoriteProduct> {
        return favoritesDao.getAllFavoriteProducts()
    }

    override suspend fun removeAllFavoriteProducts() {
        favoritesDao.removeAllFavoriteProducts()
    }

    override suspend fun isFavoriteProduct(productID: Long): Boolean {
        return favoritesDao.isFavouriteProduct(productID)
    }

    override suspend fun updateFavoriteProduct(favoriteProduct: FavoriteProduct): Int {
        return favoritesDao.updateFavoriteProduct(favoriteProduct)
    }

    override suspend fun getFlowFavoriteProducts(): Flow<List<FavoriteProduct>> {
        return favoritesDao.getFlowFavoriteProducts()
    }

    override suspend fun addProductToCart(cartItem: CartItem): Long {
        return cartDao.insertItemToCart(cartItem)
    }

    override suspend fun removeProductFromCart(productVariantID: Long): Int {
        return cartDao.removeItemFromCart(productVariantID)
    }

    override suspend fun getAllCartProducts(): List<CartItem> {
        return cartDao.getAllItemsInCart()
    }

    override suspend fun removeAllCartProducts(): Int {
        return cartDao.removeAllItemsFromCart()
    }

    override suspend fun updateProductInCart(cartItem: CartItem) {
        cartDao.updateItemInTheCart(cartItem)
    }

    override suspend fun isProductInCart(productVariantID: Long): Boolean {
        return cartDao.isProductInCart(productVariantID)
    }

    override fun setUserIdToPrefs(userId: String) =
        prefs.setUserIdToPrefs(userId)


    override fun setAuthStateToPrefs(state: Boolean) =
        prefs.setUserAuthStateToPrefs(state)


    override fun getUserIdFromPrefs() =
         prefs.getUserIdFromPrefs()


    override fun getAuthStateFromPrefs() =
         prefs.isUserSignedInFromPrefs()

}