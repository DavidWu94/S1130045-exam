package tw.edu.pu.csim.s1130045.s1130045

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExamScreen(modifier: Modifier = Modifier, viewModel: ExamViewModel = viewModel()) {
    // 讀取螢幕寬度與高度px
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val widthPx = displayMetrics.widthPixels
    val heightPx = displayMetrics.heightPixels

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Yellow), // 黃色背景
        horizontalAlignment = Alignment.CenterHorizontally, // 圖片與文字置中
        verticalArrangement = Arrangement.Center
    ) {




        Image(
            painter = painterResource(id = R.drawable.happy), // 圖片是 happy
            contentDescription = "Happy Image"
        )
        Spacer(modifier = Modifier.height(10.dp)) // 間距高度10dp
        Text(text = "瑪利亞基金會服務大考驗")

        Spacer(modifier = Modifier.height(10.dp)) // 間距高度10dp
        Text(text = "作者：資管二A 吳岱威")

        Spacer(modifier = Modifier.height(10.dp)) // 間距高度10dp
        Text(text = "螢幕大小：$widthPx * $heightPx")

        Spacer(modifier = Modifier.height(10.dp)) // 間距高度10dp
        Text(text = "成績：0分")
    }
}