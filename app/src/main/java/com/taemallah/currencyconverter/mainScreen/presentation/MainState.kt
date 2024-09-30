package com.taemallah.currencyconverter.mainScreen.presentation

import com.taemallah.currencyconverter.mainScreen.data.database.Currency

data class MainState(
    val currencies: List<Currency> = emptyList(),
    val convertFrom : Currency? = null,
    val convertTo : Currency? = null,
    val convertValue : String = "",
    val convertResult : String = "",
    val isLoading: Boolean = false,
    val error: String? = null
){
    fun isReadyToConvert(): Boolean{
        return !(currencies.isEmpty() || convertFrom == null || convertTo == null || convertValue.isBlank())
    }
}
