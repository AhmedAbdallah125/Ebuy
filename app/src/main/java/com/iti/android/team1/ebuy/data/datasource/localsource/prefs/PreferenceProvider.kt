package com.iti.android.team1.ebuy.data.datasource.localsource.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_NAME = "E_BUY_APP"

private const val USER_ID = "USER_ID_KEY"
private const val IS_USER_SINGED_IN = "IS_USER_SINGED_IN"
private const val FAVORITES_ID = "FAVORITES_ID_KEY"
private const val CART_ID = "CART_ID_KEY"
private const val CART_NO = "CART_NO"
private const val FAVORITES_NO = "FAVORITE_NO"

private const val RUN_FIRST_TIME = "RUN_FIRST_TIME"

class PreferenceProvider private constructor() {

    companion object {

        private lateinit var sharedPref: SharedPreferences

        @Volatile
        private var preferenceProvider: PreferenceProvider? = null

        fun getInstance(context: Context): PreferenceProvider {

            return preferenceProvider ?: synchronized(this) {
                if (!::sharedPref.isInitialized)
                    sharedPref =
                        context.applicationContext.getSharedPreferences(PREF_NAME,
                            Context.MODE_PRIVATE)
                val instance = PreferenceProvider()
                preferenceProvider = instance
                instance
            }
        }
    }

    fun isUserSignedInFromPrefs(): Boolean =
        sharedPref.getBoolean(IS_USER_SINGED_IN, false)

    fun setUserAuthStateToPrefs(state: Boolean) =
        sharedPref.edit().putBoolean(IS_USER_SINGED_IN, state).apply()

    fun getUserIdFromPrefs(): String =
        sharedPref.getString(USER_ID, "").toString()

    fun setUserIdToPrefs(userId: String) =
        sharedPref.edit().putString(USER_ID, userId).apply()

    fun getFavoritesIdFromPrefs(): String =
        sharedPref.getString(FAVORITES_ID, "").toString()

    fun setFavoritesIdToPrefs(favId: String) =
        sharedPref.edit().putString(FAVORITES_ID, favId).apply()

    fun getCartIdFromPrefs(): String =
        sharedPref.getString(CART_ID, "").toString()

    fun setCartIdToPrefs(cartId: String) =
        sharedPref.edit().putString(CART_ID, cartId).apply()

    private val _noOfCart: MutableStateFlow<Int> = MutableStateFlow(getCartNo())
    val noOfCart get() = _noOfCart.asStateFlow()

    private val _noOfFavorites: MutableStateFlow<Int> = MutableStateFlow(getFavoriteNo())
    val noOfFavorites get() = _noOfFavorites.asStateFlow()

    suspend fun setFavoriteNo(favoriteNo: Int) {
        _noOfFavorites.emit(favoriteNo)
        sharedPref.edit().putInt(FAVORITES_NO, favoriteNo).apply()
    }

    private fun getFavoriteNo() =
        sharedPref.getInt(FAVORITES_NO, 0)

    suspend fun setCartNo(cartNo: Int) {
        _noOfCart.emit(cartNo)
        sharedPref.edit().putInt(CART_NO, cartNo).apply()
    }

    private fun getCartNo() =
        sharedPref.getInt(CART_NO, 0)

    fun isRunFirstTime(): Boolean = sharedPref.getBoolean(RUN_FIRST_TIME, true)

    fun setRunFirstTime() {
        sharedPref.edit().putBoolean(RUN_FIRST_TIME, false).apply()
    }
}