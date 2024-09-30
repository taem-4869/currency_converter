package com.taemallah.currencyconverter.mainScreen.data.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.net.URL
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CurrencyApiService @Inject constructor() {

    companion object {
        const val END_POINT = "https://openexchangerates.org/api/latest.json?app_id=646c826816ed44a49d1819af54da116e"
    }

    private var job : Job = Job()
    private var timer: Timer = Timer()

    @OptIn(InternalCoroutinesApi::class)
    suspend fun getCurrenciesRates(): ApiServiceResponse {
        try{
            var apiResult = ""
            job = Job()
            job.run {
                apiResult = URL(END_POINT).readText()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        if (job.isActive){
                            job.cancel(CancellationException("connection timed out"))
                        }
                    }
                },6000)
            }
            if (job.isActive)job.join()
            val resultJsonObject = JSONObject(apiResult)
            val ratesJsonObject = resultJsonObject.getJSONObject("rates")
            Log.i("kid_e", "http get request succeeded :\n$apiResult")
            val mapResult = mutableMapOf<String,Double>()
            val keys = ratesJsonObject.keys()
            keys.forEach {
                mapResult[it] = ratesJsonObject.getString(it).toDouble()
            }
            return ApiServiceResponse.Success(mapResult)

        }catch (e:Exception){
            e.printStackTrace()
            Log.i("kid_e", "http get request succeeded :\n${e.message}")
            return ApiServiceResponse.Failure(e)
         }
    }

    sealed interface ApiServiceResponse{
        data class Success(val currenciesRates : Map<String,Double>): ApiServiceResponse
        data class Failure(val exception: Exception): ApiServiceResponse
    }

}