package com.example.food.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestDao {

@Insert

fun addToFav(data:RestData)

@Delete
fun addToDelete(data:RestData)

@Query("SELECT * FROM rest")
fun getAllFav(): List<RestData>

@Query("SELECT * FROM rest where rest_id= :id")
fun getFavById(id:String): RestData


}