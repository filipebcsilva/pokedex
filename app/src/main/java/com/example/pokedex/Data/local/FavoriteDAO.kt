package com.example.pokedex.Data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(f: FavoriteModel): Long

    @Delete
    fun delete(f: FavoriteModel)

    @Query("SELECT * FROM Favorite WHERE id = :id")
    fun getById(id: Int): FavoriteModel?

    @Query("SELECT * FROM Favorite ORDER BY id")
    fun getAll(): List<FavoriteModel>
}
