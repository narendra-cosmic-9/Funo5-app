package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tools")
data class FavoriteToolEntity(
    @PrimaryKey val toolId: String,
    val favoritedAt: Long = System.currentTimeMillis()
)
