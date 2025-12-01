package tw.edu.pu.csim.s1130045.s1130045

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@Composable
fun ExamScreen(modifier: Modifier = Modifier, viewModel: ExamViewModel = viewModel()) {
    // 讀取螢幕寬度與高度px
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val widthPx = displayMetrics.widthPixels
    val heightPx = displayMetrics.heightPixels
    val density = displayMetrics.density // 取得螢幕密度

    // 初始化 ViewModel 中的螢幕尺寸
    LaunchedEffect(Unit) {
        viewModel.initService(widthPx, heightPx, density)
    }

    // 將 px 轉換為 dp
    val sizePx = 300
    val sizeDp = with(LocalDensity.current) { sizePx.toDp() }
    val serviceSizeDp = 100.dp
    val serviceSizePx = with(LocalDensity.current) { serviceSizeDp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Yellow) // 黃色背景
    ) {
        // 中間的文字與圖片
        Column(
            modifier = Modifier.align(Alignment.Center),
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
            Text(text = "成績：0分 ${viewModel.message}") // 顯示碰撞訊息
        }

        // role0: 嬰幼兒 (左邊切齊螢幕左邊，下方切齊螢幕高1/2)
        Image(
            painter = painterResource(id = R.drawable.role0),
            contentDescription = "Infant",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = -sizeDp / 2) // 往上移動一半高度，使底部對齊中心線
                .size(sizeDp)
        )

        // role1: 兒童 (右邊切齊螢幕右邊，下方切齊螢幕高1/2)
        Image(
            painter = painterResource(id = R.drawable.role1),
            contentDescription = "Child",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = -sizeDp / 2) // 往上移動一半高度，使底部對齊中心線
                .size(sizeDp)
        )

        // role2: 成人 (左邊切齊螢幕左邊，下方切齊螢幕高度)
        Image(
            painter = painterResource(id = R.drawable.role2),
            contentDescription = "Adult",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(sizeDp)
        )

        // role3: 一般民眾 (右邊切齊螢幕右邊，下方切齊螢幕高度)
        Image(
            painter = painterResource(id = R.drawable.role3),
            contentDescription = "Public",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(sizeDp)
        )
        
        // 掉落的服務圖示
        Image(
            painter = painterResource(id = viewModel.currentServiceId),
            contentDescription = "Service Icon",
            modifier = Modifier
                .offset { IntOffset(viewModel.serviceX.roundToInt() - (serviceSizePx/2).roundToInt(), viewModel.serviceY.roundToInt()) } 
                .size(serviceSizeDp) 
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.updateServiceX(dragAmount.x)
                    }
                }
        )
    }
}