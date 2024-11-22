package com.example.tipcalculator

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.components.InputField
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import com.example.tipcalculator.util.calculateTip
import com.example.tipcalculator.util.calculateTotalPerPerson
import com.example.tipcalculator.widgets.RoundIconButton
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {
//                BuildTopHeader()
                BuildMainContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipCalculatorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tip Calculator") },
                    colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
                )
            }
        ) {
            Surface(modifier = Modifier.padding(it)) {
                content()
            }
        }
    }
}


@Preview
@Composable
fun BuildTopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(15.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text("Total Per Person", style = MaterialTheme.typography.titleSmall)
            Text("$$total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview
@Composable
fun BuildMainContent() {

    val splitByState = remember {
        mutableStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    BuildBillForm(splitByState = splitByState, tipAmountState = tipAmountState, totalPerPersonState = totalPerPersonState) {

    }
}

@Composable
fun BuildBillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState:MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Column() {
        BuildTopHeader(totalPerPerson = totalPerPersonState.value)

        Surface(
            modifier = modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()

                    })
                if (validState) {
                    Row(
                        modifier = modifier
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text("Split", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(120.dp))

                        Row (
                            modifier = modifier
                                .padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ){
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitByState.value = if (splitByState.value > 1) splitByState.value - 1
                                    else 1
                                    totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                                }
                            )
                            Text(
                                "${splitByState.value}",
                                modifier = Modifier.align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp))

                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitByState.value < range.last) {
                                        splitByState.value = splitByState.value + 1
                                    }
                                    totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                                }
                            )

                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 3.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "Tip",
                            modifier = Modifier
                                .align(Alignment.CenterVertically))
                        Spacer(
                            modifier = Modifier
                                .width(200.dp)
                        )

                       Text(text = "$ ${tipAmountState.value}")
                    }

                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "$tipPercentage %")
                        Spacer(
                            modifier = Modifier
                                .height(14.dp)
                        )
                        Slider(
                            value = sliderPositionState.value,
                            onValueChange = { newValue ->
                                sliderPositionState.value = newValue
                                tipAmountState.value = calculateTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)
                                totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp),
                            steps = 5,
                            onValueChangeFinished = {}
                        )
                    }
                }
            }
        }
    }

}

