package com.taemallah.currencyconverter.mainScreen.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Currency::class
    ],
    version = 1
)
abstract class CurrencyDatabase : RoomDatabase(){
    abstract val dao : CurrencyDao
}