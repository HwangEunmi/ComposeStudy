package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial4_1Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize()) {

        StyleableTutorialText(
            text = "Remember는 초기 계산값을 저장한다. 이 값은 함수에 대해 캐시 역할을 하며" +
                    "재구성으로부터 값을 유지한다.",
            bullets = false
        )

        StyleableTutorialText(
            text = "mutableState(Counter)값을 Click Listener가 연결된 버튼 밖에서 사용하지 않기 때문에" +
                    "(관찰하지 않기 때문에) 재구성은 일어나지 않는다. " +
                    "그렇기 때문에 myVal은 버튼 내부쪽에서만 업데이트된다.",
            bullets = false
        )
        Counter1()
        Spacer(modifier = Modifier.height(8.dp))
        StyleableTutorialText(
            text = "Counter2와 Counter3은 Click Listener가 연결된 버튼 밖에서" +
                    "mutableState(counter)값을 관찰하기 때문에 재구성이 일어난다." +
                    "하지만 Couter2의 myVal은 항상 remember{0} 즉 0을 기억하고 있고" +
                    "Counter3의 myVal은 초기값이 0이기 때문에 재구성되면 0으로 초기화된다.",
            bullets = false
        )
        Counter2()
        Spacer(modifier = Modifier.height(8.dp))
        Counter3()
        Spacer(modifier = Modifier.height(8.dp))
        StyleableTutorialText(
            text = "Since **MyData** is remembered in every recomposition initial one in " +
                    "**remember** is retained. Because of this it's initial value is " +
                    "displayed in inner composition",
            bullets = false
        )
        Counter4()
    }
}

@Composable
private fun Counter1() {

    Column(
        modifier = Modifier
            .background(Orange400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        var counter by remember { mutableIntStateOf(0) }
        val myData = remember { MyData() }
        var myVal = 0

        Text("myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                // 버튼을 눌러도 재구성이 일어나지 않는다. (버튼 외부에 있는 Text 값은 갱신되지 X)
                myVal++
                myData.value = myData.value + 1
            }) {
            Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

@Composable
private fun Counter2() {

    Column(
        modifier = Modifier
            .background(Blue400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        var counter by remember { mutableIntStateOf(0) }
        val myData = remember { MyData() }
        var myVal = remember { 0 }

        Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                // myVal은 mutableState값이 아니기 때문에 재구성이 일어나도 값이 변경되지 않는다.
                myVal++
                myData.value = myData.value + 1
            }) {
            Text("myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

@Composable
private fun Counter3() {

    Column(
        modifier = Modifier
            .background(Pink400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        var counter by remember { mutableIntStateOf(0) }
        val myData = remember { MyData() }
        var myVal = 0

        Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                myVal++
                myData.value = myData.value + 1
            }) {
            Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

@Composable
private fun Counter4() {

    Column(
        modifier = Modifier
            .background(Green400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        var counter by remember { mutableIntStateOf(0) }
        var myData = remember { MyData() }
        var myVal = remember { 0 }

        Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                myVal++
                // myData.value가 아닌 myData 객체를 다시 대입하는 것이므로 증가되지 않는다.
                myData = MyData(myData.value + 1)
            }) {
            Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

class MyData(var value: Int = 0)
