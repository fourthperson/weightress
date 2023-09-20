package iak.wrc.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import iak.wrc.presentation.theme.WcTheme
import iak.wrc.presentation.theme.rubikFontFamily
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WcTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    // bottom sheet stuff
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

    val alertMessage = mainViewModel.alertMessage.observeAsState(initial = "")
    val showAlert = mainViewModel.showAlert.observeAsState(initial = false)

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
                                bottomSheetState.hide()
                                mainViewModel.validate(weightText, notesText)
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
        if (showAlert.value == true && alertMessage.value != "") {
            WRAlert(
                onDismissRequest = {
                    mainViewModel.showAlert.postValue(false)
                    mainViewModel.alertMessage.postValue("")
                },
                dialogTitle = "Error",
                dialogText = alertMessage.value,
                onConfirmation = {
                    mainViewModel.showAlert.postValue(false)
                    mainViewModel.alertMessage.postValue("")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}