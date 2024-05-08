package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.model.Place
import com.smarttoolfactory.tutorial1_1basics.model.places
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.PlacesToBookComponent
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

@Preview
@Composable
fun Tutorial4_5_2Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "1-) 리컴포지션이 발생할때마다 SideEffect 함수가 트리거된다."
        )
        SideEffectExample()

        StyleableTutorialText(
            text = "2-) produceState는 Compose State가 아닌 것을 Compose의 State로 변환한다."
        )
        ProduceStateSampleButton()

        val lazyListState: LazyListState = rememberLazyListState()
        StyleableTutorialText(
            text = "3-)derivedStateOf를 사용하면 계산에 사용된 상태 중 하나가 변경될때마다" +
                    "계산을 다시 한다."
        )

        TutorialText2(text = "derivedStateOf with Int")
        DerivedStateOfExample()
        TutorialText2(text = "derivedStateOf with LazyListState")
        DerivedStateOfSample2(lazyListState)

        StyleableTutorialText(
            text = "4-)snapshotFlow는 Composable의 State를 Flow로 변환한다."
        )
        SnapshotFlowExample(lazyListState)
    }
}

/**
 * SideEffect는 Composable의 Composition이 성공적으로 되었을때 발행하는 Effect이다.
 * (즉 리컴포지션이 발생할때마다 트리거된다)
 * SideEffect는 Compose에서 관리하지 않는 객체와 Compose 내부의 데이터를 공유하기 위해 사용
 * 즉, Composite 상태를 non-Compose 코드에 게시하기 위해 사용한다.
 */
@Composable
private fun SideEffectExample() {

    val context = LocalContext.current

    // Updates composable that listens changes in value of this State
    var counterOuter by remember { mutableIntStateOf(0) }
    // Updates composable that listens changes in value of this State
    var counterInner by remember { mutableIntStateOf(0) }

    // only runs first time SideEffectSample is called
    SideEffect {
        Toast.makeText(context, "SideEffectExample()", Toast.LENGTH_SHORT).show()
    }
    Column(
        Modifier
            .background(Orange400)
            .padding(8.dp)
    ) {

        // only runs first time SideEffectSample is called
        SideEffect {
            Toast.makeText(
                context,
                "SideEffectExample() OUTER",
                Toast.LENGTH_SHORT
            ).show()
        }

        Button(onClick = { counterOuter++ }, modifier = Modifier.fillMaxWidth()) {
            SideEffect {
                Toast.makeText(
                    context,
                    "SideEffectExample() Button OUTER",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            Text(text = "Outer Composable: $counterOuter")
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(Blue400)
                .padding(8.dp)
        ) {

            SideEffect {
                Toast.makeText(
                    context,
                    "SideEffectExample() INNER",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            Button(onClick = { counterInner++ }, modifier = Modifier.fillMaxWidth()) {

                SideEffect {
                    Toast.makeText(
                        context,
                        "SideEffectExample() Button INNER",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                Text(text = "Inner Composable: $counterInner")
            }

        }
    }
}
// Composable은 선언형 프로그래밍이다.
// 선언형 프로그래밍 패러다임은 Composable 구성 순서를 보장하지 않으며 결과만을 보장한다.
// Composable이 구성되는 도중에 Composable 외부의 것이 들어갈 경우,
// Composable이 구성되지 않았으므로 initialized 되지 않았다는 오류가 뜬다.
// java.lang.illegalStateException: FocusRequester is not initialized. Here are some possible fixes:
// 따라서 Composable의 구성이 완료된 후에 Compose에서 관리하지 않는 객체(Ex. Toast)가 호출되도록
// 보장하려면 SideEffect를 사용해야 한다.


@Composable
private fun ProduceStateSampleButton() {

    var loadImage by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display a load image button when image is not loading
        OutlinedButton(
            onClick = {
                loadImage = !loadImage
            }
        ) {
            Text(text = "Click to load image with produceState $loadImage")
        }

        if (loadImage) {
            ProduceStateExample()
        }
    }
}

/**
 * ProduceState를 사용하면 Compose가 아닌 일반적인 값을 Compose의 상태로 변환할 수 있다.
 * 즉 return 값으로 State<T>를 반환할 수 있다.
 * ProduceState는 관찰하고 있는 값이 변경되면 State<T>를 반환한다.
 */
@Composable
private fun ProduceStateExample() {
    val context = LocalContext.current

    val url = "www.example.com"
    val imageRepository = remember { ImageRepository() }

    val imageState = loadNetworkImage(url = url, imageRepository)

    when (imageState.value) {
        is Result.Loading -> {
            println("🔥 ProduceStateExample() Result.Loading")
            Toast.makeText(context, "🔥 ProduceStateExample() Result.Loading", Toast.LENGTH_SHORT)
                .show()
            CircularProgressIndicator()
        }

        is Result.Error -> {
            println("❌ ProduceStateExample() Result.Error")
            Toast.makeText(context, "❌ ProduceStateExample() Result.Error", Toast.LENGTH_SHORT)
                .show()

            Image(imageVector = Icons.Default.Error, contentDescription = null)
        }

        is Result.Success -> {
            println("✅ ProduceStateExample() Result.Success")
            Toast.makeText(context, "✅ ProduceStateExample() Result.Success", Toast.LENGTH_SHORT)
                .show()

            val image = (imageState.value as Result.Success).image

            Image(
                painterResource(id = image.imageIdRes),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun loadNetworkImage(
    url: String,
    imageRepository: ImageRepository
): State<Result> {

    // Result.Loading을 초기값으로 하여 State<T>를 생성한다.
    // 'url' 또는 'imageRepository'가 변경되면 실행중인 프로듀서가 취소되고
    // 새 입력값으로 다시 시작된다.
    return produceState<Result>(initialValue = Result.Loading, url, imageRepository) {

        // In a coroutine, can make suspend calls
        val image = imageRepository.load(url)

        // Update State with either an Error or Success result.
        // This will trigger a recomposition where this State is read
        value = if (image == null) {
            Result.Error
        } else {
            Result.Success(image)
        }
    }
}

sealed class Result {
    data object Loading : Result()
    data object Error : Result()
    class Success(val image: ImageRes) : Result()
}

class ImageRes(val imageIdRes: Int)

class ImageRepository {

    /**
     * Returns a drawable resource or null to simulate Result with Success or Error states
     */
    suspend fun load(url: String): ImageRes? {
        delay(2000)

        // Random is added to return null if get a random number that is zero.
        // Possibility of getting null is 1/4
        return if (Random.nextInt(until = 4) > 0) {

            val images = listOf(
                R.drawable.avatar_1_raster,
                R.drawable.avatar_2_raster,
                R.drawable.avatar_3_raster,
                R.drawable.avatar_4_raster,
                R.drawable.avatar_5_raster,
                R.drawable.avatar_6_raster,
            )

            // Load a random id each time load function is called
            ImageRes(images[Random.nextInt(images.size)])
        } else {
            null
        }
    }
}

@Composable
private fun DerivedStateOfExample() {

    var numberOfItems by remember {
        mutableIntStateOf(0)
    }

    // derivedStateOf는 trigger 요건으로 사용할 항목들을 param으로 지정하고
    // 해당 param의 항목이 변경되면 derivedStateOf로 감싸진 block을 처리한 후
    // 상태를 변경한다.
    val derivedStateMax by remember {
        derivedStateOf {
            numberOfItems > 5
        }
    }
    val derivedStateMin by remember {
        derivedStateOf {
            numberOfItems > 0
        }
    }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Amount to buy: $numberOfItems", modifier = Modifier.weight(1f))
            IconButton(onClick = { numberOfItems++ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { if (derivedStateMin) numberOfItems-- }) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "remove")
            }
        }

        println(
            "🤔 COMPOSING..." +
                    "numberOfItems: $numberOfItems, " +
                    "derivedStateMax: $derivedStateMax, " +
                    "derivedStateMin: $derivedStateMin"
        )
        if (derivedStateMax) {
            Text("You cannot buy more than 5 items", color = Color(0xffE53935))
        }
    }
}

@Composable
private fun DerivedStateOfSample2(scrollState: LazyListState) {

    val coroutineScope = rememberCoroutineScope()

    val firstItemVisible by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex != 0 }
    }

    Box {
        LazyRow(
            state = scrollState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items(places) { place: Place ->
                    PlacesToBookComponent(place = place)
                }
            }
        )

        if (firstItemVisible) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd),
                backgroundColor = Color(0xffE53935)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun SnapshotFlowExample(scrollState: LazyListState) {

    var sentLogCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                sentLogCount++
            }
    }

    if (sentLogCount > 0) {
        StyleableTutorialText(
            text = "💗 SnapshotFlowSample collect called **$sentLogCount** times " +
                    "after firstVisibleItemIndex > map threshold",
            bullets = false
        )
    }
}