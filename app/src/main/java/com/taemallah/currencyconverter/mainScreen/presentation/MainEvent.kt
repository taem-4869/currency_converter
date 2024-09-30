package com.taemallah.currencyconverter.mainScreen.presentation

import com.taemallah.currencyconverter.mainScreen.data.database.Currency

sealed interface MainEvent{
    data class SetConvertFrom(val convertFromCurrency: Currency): MainEvent
    data class SetConvertTo(val convertToCurrency: Currency): MainEvent
    data class SetConvertValue(val convertValue: String): MainEvent
    data object SwapConvertUnits: MainEvent
    data object SwapConvertValues: MainEvent
}
