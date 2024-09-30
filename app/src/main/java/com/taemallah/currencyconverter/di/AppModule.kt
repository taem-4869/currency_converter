package com.taemallah.currencyconverter.di

import android.content.Context
import androidx.room.Room
import com.taemallah.currencyconverter.mainScreen.data.database.CurrencyDatabase
import com.taemallah.currencyconverter.mainScreen.data.network.CurrencyApiService
import com.taemallah.currencyconverter.mainScreen.data.repository.CurrencyRepoImpl
import com.taemallah.currencyconverter.mainScreen.domain.repository.CurrencyRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesCurrencyDatabase(@ApplicationContext context: Context) : CurrencyDatabase = Room
        .databaseBuilder(context,CurrencyDatabase::class.java,"currencies_rates.db")
        .build()

    @Provides
    @Singleton
    fun providesCurrencyRepo(database: CurrencyDatabase, api: CurrencyApiService, @ApplicationContext context: Context) : CurrencyRepo = CurrencyRepoImpl(context, database,api)

}