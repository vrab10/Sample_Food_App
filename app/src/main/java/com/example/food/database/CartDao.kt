package com.example.food.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {

    @Insert
    fun insertCart(cartEntity: CartEntity)

    @Delete
    fun deleteCart(cartEntity: CartEntity)

    //only the above two are done by default

    @Query("SELECT * from carts")
    fun getAllCarts(): List<CartEntity>

    @Query("DELETE FROM carts WHERE res_id = :resId")
    fun deleteOrders(resId: String)


}