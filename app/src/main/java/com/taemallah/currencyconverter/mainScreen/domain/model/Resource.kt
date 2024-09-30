package com.taemallah.currencyconverter.mainScreen.domain.model

sealed class Resource <out R> {
    data class Success <out R>(val result: R): Resource<R>()
    data class Failure <out R>(val exception: Exception, val oldResult: R?): Resource<R>()
}