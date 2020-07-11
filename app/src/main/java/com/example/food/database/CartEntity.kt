package com.example.food.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="carts")
data class CartEntity (
    @PrimaryKey val  res_id: String,
    @ColumnInfo(name = "items") val  items: String
    )