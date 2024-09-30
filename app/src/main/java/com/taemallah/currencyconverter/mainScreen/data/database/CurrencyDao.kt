package com.taemallah.currencyconverter.mainScreen.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CurrencyDao {

    @Upsert
    suspend fun upsertCurrency(currency: Currency)

    @Upsert
    suspend fun upsertAll(currencies: List<Currency>)

    @Query("DELETE FROM Currency")
    suspend fun clearAll()

    @Query("SELECT * FROM CURRENCY ORDER BY CODE ASC")
    fun getCurrencies():List<Currency>

    @Query("SELECT * FROM CURRENCY WHERE CODE = :code")
    fun getCurrencyByCode(code : String):Currency?
}