package com.taemallah.currencyconverter.mainScreen.domain.repository

import com.taemallah.currencyconverter.mainScreen.data.database.Currency
import com.taemallah.currencyconverter.mainScreen.domain.model.Resource

interface CurrencyRepo {

    suspend fun getCurrencies(): Resource<List<Currency>>
    suspend fun getCurrencyByCode(code : String): Currency?

}