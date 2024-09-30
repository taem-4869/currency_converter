package com.taemallah.currencyconverter.mainScreen.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.taemallah.currencyconverter.mainScreen.data.database.Currency
import com.taemallah.currencyconverter.mainScreen.data.database.CurrencyDatabase
import com.taemallah.currencyconverter.mainScreen.data.network.CurrencyApiService
import com.taemallah.currencyconverter.mainScreen.domain.model.Resource
import com.taemallah.currencyconverter.mainScreen.domain.repository.CurrencyRepo
import com.taemallah.currencyconverter.utils.setCurrencyNames
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CurrencyRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: CurrencyDatabase,
    private val api : CurrencyApiService,
) : CurrencyRepo {

    override suspend fun getCurrencies(): Resource<List<Currency>> {
        when(val response = api.getCurrenciesRates()){
            is CurrencyApiService.ApiServiceResponse.Failure -> {
                return Resource.Failure(response.exception, database.dao.getCurrencies())
            }
            is CurrencyApiService.ApiServiceResponse.Success -> {
                val newCurrencies = mapToCurrencies(response.currenciesRates)
                database.withTransaction {
                    database.dao.upsertAll(newCurrencies)
                }
                return Resource.Success(database.dao.getCurrencies())
            }
        }
    }

    override suspend fun getCurrencyByCode(code: String): Currency? {
        try {
          return database.dao.getCurrencyByCode(code)
        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun mapToCurrencies(map: Map<String,Double>): List<Currency> {
        val currencies = map.entries.map{ Currency(it.key,"",it.value) }
        return setCurrencyNames(context, currencies)
    }

}