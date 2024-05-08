package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun Tutorial4_5_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState) {

        /*
            LaunchedEffect and rememberCoroutineScope
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = 8.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp + it.calculateBottomPadding()
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {

            StyleableTutorialText(
                text = "LaunchedEffect는 Composable에서 컴포지션이 일어날때 suspend fun을 실행해주는 Composable이다." +
                        "리컴포지션은 Composable의 State가 바뀔때마다 일어나므로, 만약 매번 리컴포지션이 일어날때마다 이전 LaunchedEffect가 " +
                        "취소되고 다시 수행되면 매우 비효율적일 것이다. 이를 해결하기 위해 LaunchedEffect는 key라고 불리는 기준값을 두어" +
                        "key가 바뀔때만 LaunchedEffect의 suspend fun을 취소하고 재실행한다.",
                bullets = false
            )
            LaunchedEffectExample(scaffoldState)

            CoroutineScopeExample(scaffoldState)

            /*
                rememberUpdatedState
             */
            StyleableTutorialText(
                text = "rememberUpdatedState unlike remember updates calculation" +
                        " when a composable is recomposed.",
                bullets = false
            )
            UpdatedRememberExample()
            RememberUpdatedStateSample2()
            /*
                DisposableEffect samples
             */
            StyleableTutorialText(
                text = "DisposableEffect is good for cleaning things before " +
                        "a composable exits composition",
                bullets = false
            )
            DisposableEffectButton()
            DisposableEffectLifecycleButton()
        }
    }

}

/**
 * '조건이 true 일때, 컴포지션에 LaunchedEffect 추가하기'
 * 즉 조건이 충족되면 remember가 컴포지션에 추가되고
 * 조건이 충족되지 않으면 제거된다.
 */
@Composable
private fun LaunchedEffectExample(scaffoldState: ScaffoldState) {
    var counter by remember { mutableIntStateOf(0) }

    if (counter > 0 && counter % 3 == 0) {
        // LaunchedEffect는 scaffoldState.snackbarHostState가 변경되면
        // 취소하고 다시 실행한다.
        LaunchedEffect(scaffoldState.snackbarHostState) {
            // 코루틴을 사용하여 snackbar를 표시한다.
            // 코루틴이 취소되면 snackbar가 자동으로 사라진다.
            scaffoldState.snackbarHostState.showSnackbar("LaunchedEffect snackbar")
        }
    }

    // This button increase counter that will trigger LaunchedEffect
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            counter++
        }
    ) {
        Text("LaunchedEffect Counter $counter")
    }
}

@Composable
private fun CoroutineScopeExample(
    scaffoldState: ScaffoldState
) {

    // Creates a CoroutineScope bound to TutorialContent
    val scope = rememberCoroutineScope()

    // This button increase counter that will trigger CoroutineScope
    var counter by remember { mutableIntStateOf(0) }

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            counter++
            if (counter > 0 && counter % 3 == 0) {
                scope.launch {
                    // Button 클릭시 snackbar를 띄우려면 onClick = {...} 내부에서 snackbar를 show 해야 한다.
                    // 이는 composable function의 내부가 아니기 때문에 rememberCoroutineScope로 coroutine scope를 생성한다.
                    scaffoldState.snackbarHostState.showSnackbar("CoroutineScope snackbar")
                }
            }

        }
    ) {
        Text("CoroutineScope Counter $counter")
    }
}

@Composable
private fun UpdatedRememberExample() {
    var myInput by remember {
        mutableIntStateOf(0)
    }

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            myInput++

        }
    ) {
        Text("Increase rememberInput: $myInput")
    }
    Calculation(input = myInput)
}

/*
    rememberUpdateState
 */

/**
 * rememberUpdateStaet와 remember의 차이점에 관한 내용이다.
 * remember와 rememberUpdatedState는 둘다 이전 구성때의 값을 저장한다.
 * 그러나 rememberUpdatedState는 remember와 달리 재구성시 함수에 다른 값이 입력되면 저장된 값을 갱신한다.
 */
@Composable
private fun Calculation(input: Int) {
    // val 타입의 input값을 기본값으로 넣고 있지만 그 외의 set 작업은 하지 않고 있기 때문에
    // remember로 감쌀 경우 값이 갱신되지 않는다.
    // 반면에 rememberUpdatedState는 리컴포션시 랩핑되어있는 값을 최신값으로 갱신한다.
    val rememberUpdatedStateInput by rememberUpdatedState(input)
    val rememberedInput = remember { input }
    Text("updatedInput: $rememberUpdatedStateInput, rememberedInput: $rememberedInput")
}
// remember : 이전 구성때 저장한 값을 재구성때 사용할 수 있도록 해준다.
// (즉 재구성을 할때는 값을 새롭게 생성하지 않고 기존에 저장되어 있던 데이터를 반환한다)
// 이때 remember의 상태는 remember의 value를 직접 수정해주는 것으로만 가능하다.
// 즉, 다시 하고 싶지 않은 비싼 작업을 수행해야 할 때 or 재구성되어도 데이터를 보존해야 할 때 사용하는 것이 좋다.

// rememberUpdatedState : remember의 value를 직접 수정해주는 것과 동일한 역할을 한다.
// 즉 값이 들어올때마다 새로운 value값을 remember된 mutableState에 직접 apply 해준다.

/**
 * In this example we set a lambda to be invoked after a calculation that takes time to complete
 * while calculation running if our lambda gets updated `rememberUpdatedState` makes sure
 * that latest lambda is invoked
 */
@Composable
private fun RememberUpdatedStateSample2() {

    val context = LocalContext.current

    var showCalculation by remember { mutableStateOf(true) }
    val radioOptions = listOf("Option🍒", "Option🍏", "Option🎃")

    val (selectedOption: String, onOptionsSelected: (String) -> Unit) = remember {
        mutableStateOf(radioOptions[0])
    }

    Column {

        radioOptions.forEach { text ->
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                if (!showCalculation) {
                                    showCalculation = true
                                }
                                onOptionsSelected(text)
                            },
                            role = Role.RadioButton
                        )
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(selected = (text == selectedOption), onClick = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = text)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showCalculation) {

            println("📝 Invoking calculation2 with option: $selectedOption")

            Calculation2 {
                showCalculation = false
                Toast.makeText(
                    context,
                    "Calculation2 $it result: $selectedOption",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}

/**
 * LaunchedEffect restarts when one of the key parameters changes.
 * However, in some situations you might want to capture a value in your effect that,
 * if it changes, you do not want the effect to restart.
 * In order to do this, it is required to use rememberUpdatedState to create a reference
 * to this value which can be captured and updated. This approach is helpful for effects that
 * contain long-lived operations that may be expensive or prohibitive to recreate and restart.
 */
@Composable
private fun Calculation2(operation: (String) -> Unit) {

    println("🤔 Calculation2(): operation: $operation")
    // This returns the updated operation if we recompose with new operation
    val currentOperation by rememberUpdatedState(newValue = operation)
    // This one returns the initial operation this composable enters composition
    val rememberedOperation = remember { operation }

    // 🔥 This LaunchedEffect block only gets called once, not called on each recomposition
    LaunchedEffect(key1 = true, block = {
        delay(4000)
        currentOperation("rememberUpdatedState")
        rememberedOperation("remember")
    })

    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(color = getRandomColor())
    }
}


/*
    DisposableEffect
 */
@Composable
private fun DisposableEffectButton() {
    var showDisposableEffectSample by remember { mutableStateOf(false) }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            showDisposableEffectSample = !showDisposableEffectSample

        }
    ) {
        Text("Display DisposableEffect sample")
    }

    if (showDisposableEffectSample) {
        DisposableEffectExample()
    }
}

/**
 * DisposableEffect는 Composable이 Dispose된 후에 정리해야 할 Side Effect가 있는 경우에
 * 사용하는 Effect이다.
 * 즉 Composable의 Lifecycle에 맞춰 정리되어야 하는 리스너나 작업이 있는 경우에
 * 리스너나 작업을 제거하기 위해서 사용되는 Effect이다.
 */
@Composable
private fun DisposableEffectExample() {

    val context = LocalContext.current

    DisposableEffect(
        key1 = Unit,
        effect = {
            // Composable이 시작될 때 호출된다.
            Toast.makeText(
                context,
                "DisposableEffectSample composition ENTER",
                Toast.LENGTH_SHORT
            ).show()

            // Composable이 제거 즉, Dispose 될 때 호출된다.
            // (보통 Dispose 될 때 콜백제거 등 Dispose 되어야 하는 로직을 수행)
            onDispose {
                Toast.makeText(
                    context,
                    "DisposableEffectSample composition EXIT",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }
        }
    )

    Column(modifier = Modifier.background(Color(0xffFFB300))) {
        Text(
            text = "Disposable Effect Enter/Exit sample",
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }

}

@Composable
private fun DisposableEffectLifecycleButton() {
    var showDisposableEffectLifeCycle by remember { mutableStateOf(false) }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            showDisposableEffectLifeCycle = !showDisposableEffectLifeCycle

        }
    ) {
        Text("Display DisposableEffect with LifeCycle")
    }

    if (showDisposableEffectLifeCycle) {

        val context = LocalContext.current

        DisposableEffectWithLifeCycle(
            onResume = {
                Toast.makeText(
                    context,
                    "DisposableEffectWithLifeCycle onResume()",
                    Toast.LENGTH_SHORT
                )
                    .show()
            },
            onPause = {
                Toast.makeText(
                    context,
                    "DisposableEffectWithLifeCycle onPause()",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        )
    }
}

@Composable
private fun DisposableEffectWithLifeCycle(
    onResume: () -> Unit,
    onPause: () -> Unit,
) {

    val context = LocalContext.current

    // Safely update the current lambdas when a new one is provided
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    Toast.makeText(
        context,
        "DisposableEffectWithLifeCycle composition ENTER",
        Toast.LENGTH_SHORT
    ).show()

    val currentOnResume by rememberUpdatedState(onResume)
    val currentOnPause by rememberUpdatedState(onPause)

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for lifecycle events
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_CREATE",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Lifecycle.Event.ON_START -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_START",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Lifecycle.Event.ON_RESUME -> {
                    currentOnResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    currentOnPause()
                }
                Lifecycle.Event.ON_STOP -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_STOP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_DESTROY",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

            Toast.makeText(
                context,
                "DisposableEffectWithLifeCycle composition EXIT",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    Column(modifier = Modifier.background(Color(0xff03A9F4))) {
        Text(
            text = "Disposable Effect with lifecycle",
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

