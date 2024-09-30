package com.taemallah.currencyconverter.mainScreen.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taemallah.currencyconverter.mainScreen.data.database.Currency
import com.taemallah.currencyconverter.mainScreen.domain.model.Resource
import com.taemallah.currencyconverter.mainScreen.domain.repository.CurrencyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: CurrencyRepo,
) : ViewModel() {
    private val _convertFromCode = savedStateHandle.getStateFlow(KEY_CONVERT_FROM_CODE,"")
    private val _convertToCode = savedStateHandle.getStateFlow(KEY_CONVERT_TO_CODE,"")
    private val _convertValue = savedStateHandle.getStateFlow(KEY_CONVERT_VALUE,"")
    private val _currenciesCodes = savedStateHandle.getStateFlow(KEY_CURRENCIES, emptyList<String>())
    private val _state = MutableStateFlow(MainState())
    val state = combine(_convertFromCode,_convertToCode,_convertValue,_state)
    {convertFromCode, convertToCode, convertValue, state->
        val convertFrom = getCurrencyFromCode(convertFromCode)
        val convertTo = getCurrencyFromCode(convertToCode)
        state.copy(
            convertFrom = convertFrom,
            convertTo = convertTo,
            convertValue = convertValue,
            convertResult = convert(convertValue, convertFrom, convertTo, state.currencies)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainState())

    init {
        load()
        collectCurrenciesByCodes()
    }

    fun onEvent(event: MainEvent){
        when(event){
            is MainEvent.SetConvertFrom -> {
                try {
                    savedStateHandle[KEY_CONVERT_FROM_CODE] = event.convertFromCurrency.code
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            is MainEvent.SetConvertTo ->{
                try {
                    savedStateHandle[KEY_CONVERT_TO_CODE] = event.convertToCurrency.code
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            is MainEvent.SetConvertValue -> {
                try {
                    if (event.convertValue.isNotBlank())
                        event.convertValue.toDouble()
                    savedStateHandle[KEY_CONVERT_VALUE] = event.convertValue
                }catch (_:Exception){}
            }

            MainEvent.SwapConvertUnits -> {
                val oldFromCurrency = state.value.convertFrom
                savedStateHandle[KEY_CONVERT_FROM_CODE] = state.value.convertTo?.code
                savedStateHandle[KEY_CONVERT_TO_CODE] = oldFromCurrency?.code
            }

            MainEvent.SwapConvertValues -> {
                try {
                    if (state.value.convertResult.isNotBlank())
                        state.value.convertResult.toDouble()
                    savedStateHandle[KEY_CONVERT_VALUE] = state.value.convertResult
                }catch (_:Exception){}
            }
        }
    }

    private fun load(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(isLoading = true)
            }
            when(val response = repo.getCurrencies()){
                is Resource.Failure -> {
                    if (response.oldResult!=null){
                        savedStateHandle[KEY_CURRENCIES] = response.oldResult.map { it.code }
                    }
                    _state.update {
                        it.copy(
                            error = "Error Occurred\n"+response.exception.message,
                            isLoading = false,
                        )
                    }
                }
                is Resource.Success -> {
                    savedStateHandle[KEY_CURRENCIES] = response.result.map { it.code }
                    _state.update {
                        it.copy(
                            error = null,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    private fun collectCurrenciesByCodes() {
        viewModelScope.launch(Dispatchers.IO){
            try {
                _currenciesCodes.collect{codes->
                    val currencies = codes.mapNotNull { repo.getCurrencyByCode(it) }
                    _state.update {
                        it.copy(
                            currencies = currencies
                        )
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun getCurrencyFromCode(code : String): Currency? {
        return state.value.currencies.find { it.code == code }
    }
    
    private fun convert(
        convertValue: String,
        convertFrom: Currency?,
        convertTo: Currency?,
        currencies: List<Currency>
    ): String {
        try {
            if ((currencies.isEmpty() || convertFrom == null || convertTo == null || convertValue.isBlank()))
                return ""
            val result = convertTo.fromBase(
                convertFrom.toBase(convertValue.toDouble())
            ).toString()
            return if (result.length<9) result else result.substring(0..8)
        }catch (e:Exception){
            Log.e("kid_e","error from viewModel convert : \n${e.message}")
            return ""
        }
    }

    companion object{
        private const val KEY_CONVERT_FROM_CODE = "fromCode"
        private const val KEY_CONVERT_TO_CODE = "toCode"
        private const val KEY_CONVERT_VALUE = "convertValue"
        private const val KEY_CURRENCIES = "currencies"
    }

}