package iak.wrc.presentation.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import iak.wrc.domain.entity.Weight
import iak.wrc.presentation.ui.MainViewModel
import iak.wrc.presentation.ui.theme.rubikFontFamily
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    // bottom sheet
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet: Boolean by remember {
        mutableStateOf(false)
    }
    // text fields
    var weightText: String by remember {
        mutableStateOf("")
    }
    var notesText: String by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        mainViewModel.loadHistory()
    }

    val alertMessage by mainViewModel.alertMessage.observeAsState(initial = "")
    val showAlert by mainViewModel.showAlert.observeAsState(initial = false)
    val history by mainViewModel.history.observeAsState(initial = ArrayList())

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        "Record Weight",
                        fontFamily = rubikFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = { showBottomSheet = true }
            )
        }
    ) { _ ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${history?.size?.toString() ?: "null"} Records",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Text(
                text = "History",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            if (history != null) {
                WRListView(weights = history!!)
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = bottomSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Record Weight",
                            fontFamily = rubikFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(PaddingValues(bottom = 16.dp))
                        )
                        TextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = {
                                Text(
                                    "Weight in Kg",
                                    fontFamily = rubikFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                        )
                        TextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            label = {
                                Text(
                                    "Notes",
                                    fontFamily = rubikFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                        )
                        Button(
                            modifier = Modifier.padding(PaddingValues(top = 16.dp, bottom = 16.dp)),
                            onClick = {
                                scope.launch {
                                    val valid = mainViewModel.validate(weightText)
                                    if (valid) {
                                        mainViewModel.recordWeight(weightText, notesText)
                                        bottomSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            },
                        ) {
                            Text(
                                "Record",
                                fontFamily = rubikFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            if (showAlert && alertMessage != "") {
                WRAlert(
                    onDismissRequest = {
                        mainViewModel.showAlert.postValue(false)
                        mainViewModel.alertMessage.postValue("")
                    },
                    dialogTitle = "Error",
                    dialogText = alertMessage,
                    onConfirmation = {
                        mainViewModel.showAlert.postValue(false)
                        mainViewModel.alertMessage.postValue("")
                    }
                )
            }
        }
    }
}

@Composable
fun WRAlert(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun WRListView(weights: List<Weight>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(weights.size) { index ->
            WeightRow(weight = weights[index])
        }
    }
}

@Composable
fun WeightRow(weight: Weight) {
    val padding: Dp = 8.dp
    Column(modifier = Modifier.clickable { }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDate(weight.date),
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
            Text(
                text = "${weight.weight} Kgs",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        fontFamily = rubikFontFamily,
                        fontSize = 13.sp
                    )
                ) {
                    append("Notes: ")
                }
                withStyle(
                    style = SpanStyle(
                        fontFamily = rubikFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp
                    )
                ) {
                    append(weight.notes)
                }
            },
            modifier = Modifier.padding(start = padding, end = padding, bottom = padding),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Divider()
    }
}

fun formatDate(timestamp: Long): String {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.timeInMillis = timestamp
    return android.text.format.DateFormat.format("E, dd MMM yyyy HH:mm:ss", calendar).toString()
}