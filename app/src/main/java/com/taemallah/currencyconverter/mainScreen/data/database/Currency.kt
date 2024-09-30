package com.taemallah.currencyconverter.mainScreen.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey(autoGenerate = false)
    val code : String,
    val name: String,
    val rate: Double
){
    fun fromBase(valueUsd : Double):Double{
        return valueUsd * rate
    }
    fun toBase(value : Double):Double{
        return value / rate
    }
    fun getFullName(): String{
        return "$code : $name"
    }
}
