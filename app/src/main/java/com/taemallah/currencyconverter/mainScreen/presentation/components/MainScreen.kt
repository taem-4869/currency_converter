package com.taemallah.currencyconverter.mainScreen.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taemallah.currencyconverter.R
import com.taemallah.currencyconverter.mainScreen.presentation.MainEvent
import com.taemallah.currencyconverter.mainScreen.presentation.MainState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(state: MainState, onEvent: (MainEvent)->Unit) {
    Scaffold {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.currency_converter)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.primary)
                        ),
                        RoundedCornerShape(10)
                    )
                    .shadow(
                        10.dp,
                        RoundedCornerShape(10),
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary,
                    )
            )
            AnimatedContent(
                targetState = state,
                label = "",
                contentAlignment = Alignment.Center,
            ) {
                if (it.isLoading){
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else if (it.error!=null){
                    Text(
                        text = it.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ConvertFromRow(state = state, onEvent = onEvent)
                SwapUnitsRow(onEvent = onEvent)
                ConvertToRow(state = state, onEvent = onEvent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertFromRow(state: MainState, onEvent: (MainEvent)-> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ){
        var isExpandedFromMenu by remember {
            mutableStateOf(false)
        }
        ExposedDropdownMenuBox(
            expanded =isExpandedFromMenu,
            onExpandedChange ={
                isExpandedFromMenu=it
            },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = state.convertFrom?.code?:"",
                onValueChange = {},
                label = {
                    Text(
                        text = stringResource(R.string.From),
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                },
                readOnly = true,
                singleLine = true,
                maxLines = 1,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedFromMenu)},
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .clip(RoundedCornerShape(10))
            )
            ExposedDropdownMenu(
                expanded = isExpandedFromMenu,
                onDismissRequest = { isExpandedFromMenu=!isExpandedFromMenu },
                modifier= Modifier.clip(RoundedCornerShape(10))) {
                state.currencies.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.code) },
                        onClick = {
                            onEvent(MainEvent.SetConvertFrom(it))
                            isExpandedFromMenu=false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.convertValue,
            onValueChange = {onEvent(MainEvent.SetConvertValue(it))},
            label = {
                Text(
                    text = state.convertFrom?.name ?: "",
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .clip(RoundedCornerShape(10))
                .weight(2f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertToRow(state: MainState, onEvent: (MainEvent)-> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ){
        var isExpandedToMenu by remember {
            mutableStateOf(false)
        }
        ExposedDropdownMenuBox(
            expanded =isExpandedToMenu,
            onExpandedChange ={
                isExpandedToMenu=it
            },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = state.convertTo?.code?:"",
                onValueChange = {},
                label = {
                    Text(
                        text = stringResource(R.string.to),
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                },
                readOnly = true,
                singleLine = true,
                maxLines = 1,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedToMenu)},
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .clip(RoundedCornerShape(10))
            )
            ExposedDropdownMenu(
                expanded = isExpandedToMenu,
                onDismissRequest = { isExpandedToMenu=false },
                modifier= Modifier.clip(RoundedCornerShape(10))) {
                state.currencies.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.code) },
                        onClick = {
                            onEvent(MainEvent.SetConvertTo(it))
                            isExpandedToMenu=false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.convertResult,
            onValueChange = {},
            label = {
                Text(
                    text = state.convertTo?.name ?: "",
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
            },
            readOnly = true,
            singleLine = true,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .clip(RoundedCornerShape(10))
                .weight(2f)
        )
    }
}

@Composable
fun SwapUnitsRow(onEvent: (MainEvent)-> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        var reverseAnim by remember {
            mutableStateOf(false)
        }
        val rotation = remember{ derivedStateOf { if (reverseAnim) 540f else 0f } }
        val anim by animateFloatAsState(targetValue = rotation.value, label = "", animationSpec = tween(1000))
        Icon(
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .rotate(anim)
                .clickable {
                    reverseAnim = !reverseAnim
                    onEvent(MainEvent.SwapConvertUnits)
                }
        )
    }
}
