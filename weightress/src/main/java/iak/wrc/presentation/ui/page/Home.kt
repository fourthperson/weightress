package iak.wrc.presentation.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.AccessibilityConfig
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import iak.wrc.domain.entity.Weight
import iak.wrc.presentation.ui.theme.rubikFontFamily
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    // bottom sheet
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet: Boolean by remember { mutableStateOf(false) }
    // text fields
    var weightText: String by remember { mutableStateOf("") }
    var notesText: String by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        homeViewModel.loadHistory()
    }

    val alertMessage by homeViewModel.alertMessage.observeAsState(initial = "")
    val showAlert by homeViewModel.showAlert.observeAsState(initial = false)
    val history by homeViewModel.history.observeAsState(initial = ArrayList())

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
                text = "Weightress",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "weight tracking, served right",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            if (history.isEmpty()) EmptyChart() else WeightChart(weights = history.reversed())
            Text(
                text = "Weight History",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Text(
                text = "${history.size} Records",
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
            WRListView(weights = history)
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
                                    val valid = homeViewModel.validate(weightText)
                                    if (valid) {
                                        homeViewModel.recordWeight(weightText, notesText)
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
                        homeViewModel.showAlert.postValue(false)
                        homeViewModel.alertMessage.postValue("")
                    },
                    dialogTitle = "Error",
                    dialogText = alertMessage,
                    onConfirmation = {
                        homeViewModel.showAlert.postValue(false)
                        homeViewModel.alertMessage.postValue("")
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun EmptyChart() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
    Text(
        "Start by recording your weight",
        fontFamily = rubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .wrapContentSize()
    )
}

@Composable
fun WeightChart(
    weights: List<Weight>,
    mainViewModel: HomeViewModel = hiltViewModel()
) {
    val pointsData = mainViewModel.chartPoints(weights)

    val xAxisData = AxisData.Builder()
        .axisStepSize(75.0.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i -> mainViewModel.chartDate(weights[i].date) }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(pointsData.size)
        .backgroundColor(Color.Red)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        lineType = LineType.SmoothCurve(),
                        color = MaterialTheme.colorScheme.primary
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    ShadowUnderLine(
                        color = MaterialTheme.colorScheme.tertiary,

                        ),
                    SelectionHighlightPopUp()
                )
            )
        ),
        yAxisData = yAxisData,
        xAxisData = xAxisData,
        gridLines = GridLines(color = Color.Transparent),
        backgroundColor = Color.Transparent,
        accessibilityConfig = AccessibilityConfig()

    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
                top = 12.dp
            ),
        lineChartData = lineChartData
    )
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
fun WeightRow(
    weight: Weight,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val padding: Dp = 8.dp
    Column(modifier = Modifier.clickable { }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = viewModel.listDate(weight.date),
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