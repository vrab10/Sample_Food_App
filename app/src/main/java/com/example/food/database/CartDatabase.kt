package com.example.food.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [CartEntity:: class,RestData:: class],version=1)

abstract class CartDatabase: RoomDatabase() {

        abstract fun cartDao() :CartDao
        abstract fun restDao() :RestDao

}