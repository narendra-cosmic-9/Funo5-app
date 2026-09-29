package com.example.data

import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    val allFavorites: Flow<List<FavoriteToolEntity>> = favoriteDao.getAllFavorites()

    suspend fun addFavorite(toolId: String) {
        favoriteDao.insertFavorite(FavoriteToolEntity(toolId))
    }

    suspend fun removeFavorite(toolId: String) {
        favoriteDao.deleteFavorite(toolId)
    }

    fun isFavorite(toolId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(toolId)
    }
}
