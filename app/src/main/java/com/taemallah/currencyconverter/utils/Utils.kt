package com.taemallah.currencyconverter.utils

import android.content.Context
import com.taemallah.currencyconverter.R
import com.taemallah.currencyconverter.mainScreen.data.database.Currency

fun setCurrencyNames(context: Context, currencies : List<Currency>): List<Currency> {
    val map = mutableMapOf<String,String>()
    context.resources.getStringArray(R.array.currencies).forEach {
        try{
            val items = it.split('|')
            map[items[0]] = items[1]
        }catch (_:Exception){}
    }
    val newCurrencies = currencies.map {
        it.copy(
            name = map[it.code]?:""
        )
    }
    return newCurrencies
}
