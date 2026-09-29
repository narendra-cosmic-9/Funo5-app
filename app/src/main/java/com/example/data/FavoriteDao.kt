package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_tools ORDER BY favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteToolEntity)

    @Query("DELETE FROM favorite_tools WHERE toolId = :toolId")
    suspend fun deleteFavorite(toolId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tools WHERE toolId = :toolId)")
    fun isFavorite(toolId: String): Flow<Boolean>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getRecentSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearchHistory()
}
