package tw.edu.pu.csim.s1130045.s1130045

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExamViewModel : ViewModel() {
    // 服務圖示相關狀態
    var serviceX by mutableStateOf(0f) // 水平位置 (px)
    var serviceY by mutableStateOf(0f) // 垂直位置 (px)
    var currentServiceId by mutableIntStateOf(R.drawable.service0) // 當前顯示的圖示資源 ID
    
    // 訊息狀態
    var message by mutableStateOf("")

    // 螢幕寬高 與 密度
    private var screenWidthPx = 0
    private var screenHeightPx = 0
    private var density = 1f
    
    private var isInitialized = false

    fun initService(widthPx: Int, heightPx: Int, density: Float) {
        if (!isInitialized) {
            this.screenWidthPx = widthPx
            this.screenHeightPx = heightPx
            this.density = density
            resetService()
            isInitialized = true
            startFalling()
        }
    }

    private fun resetService() {
        // 隨機選擇圖示
        val randomService = (0..3).random()
        currentServiceId = when (randomService) {
            0 -> R.drawable.service0
            1 -> R.drawable.service1
            2 -> R.drawable.service2
            else -> R.drawable.service3
        }
        
        // 重置位置：水平置中，垂直在上方
        serviceX = (screenWidthPx / 2).toFloat() 
        serviceY = 0f
    }

    private fun startFalling() {
        viewModelScope.launch {
            while (true) {
                delay(100) // 每0.1秒
                serviceY += 20 // 往下掉20px

                checkCollision()

                // 碰撞檢測：如果超過螢幕高度
                if (serviceY > screenHeightPx) {
                    message = "(掉到最下方)"
                    resetService()
                }
            }
        }
    }
    
    private fun checkCollision() {
        // 角色大小 300px (在 ExamScreen 中透過 .size(sizeDp) 設定，這裡需要將 dp 轉回 px)
        // 注意：ExamScreen 中使用了 300.dp (透過 sizePx.toDp() 再轉回 dp 可能會有差異，但原意應為 300px)
        // 讓我們檢查 ExamScreen： val sizePx = 300; val sizeDp = with(LocalDensity.current) { sizePx.toDp() }
        // 所以角色大小實際在 Compose 中是 300px 對應的 dp，但最終渲染大小在不同密度螢幕上就是接近 300px (如果是 Pixel perfect)
        // 但 Compose 的 .toDp() 邏輯是 px / density。所以 sizeDp 在繪製時會佔用 sizeDp * density = sizePx 像素。
        // 因此角色圖片在螢幕上的實際像素寬高就是 300px。
        val roleSizePx = 300f 
        
        // 服務圖示大小 (ExamScreen 中是 50.dp)
        val serviceSizePx = 50 * density
        
        // 服務圖示 Rect (Left, Top, Right, Bottom)
        // UI offset: x = serviceX - serviceSizePx/2 (置中), y = serviceY
        val serviceLeft = serviceX - serviceSizePx / 2
        val serviceTop = serviceY
        val serviceRight = serviceLeft + serviceSizePx
        val serviceBottom = serviceTop + serviceSizePx

        // 角色位置定義 (參考 ExamScreen)
        // Role 0: 嬰幼兒 (Alignment.CenterStart -> offset y = -sizeDp/2)
        // CenterStart: x=0, y=H/2. Top = H/2 - roleSizePx/2. Bottom = H/2 + roleSizePx/2 ?? 
        // 不，ExamScreen 中是 offset(y = -sizeDp / 2)。
        // CenterStart 的 Y 是 H/2。加上 offset -H_role/2 => Top = H/2 - H_role/2. Bottom = H/2 + H_role/2.
        // 等等，ExamScreen 註解寫： "往上移動一半高度，使底部對齊中心線"
        // 如果 Y 是 H/2，往上移 size/2 => Top = H/2 - size. Bottom = H/2.
        // 這樣 Bottom 確實對齊中心線 (H/2)。
        
        // Role 0: 嬰幼兒 
        // Left: 0
        // Right: roleSizePx
        // Top: (screenHeightPx / 2f) - roleSizePx
        // Bottom: (screenHeightPx / 2f)
        if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                0f, (screenHeightPx / 2f) - roleSizePx, roleSizePx, (screenHeightPx / 2f))) {
            message = "(碰撞嬰幼兒圖示)"
            resetService()
        }
        // Role 1: 兒童 (Alignment.CenterEnd -> offset y = -sizeDp/2)
        // Left: W - roleSizePx
        // Right: W
        // Top: H/2 - roleSizePx
        // Bottom: H/2
        else if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                (screenWidthPx - roleSizePx), (screenHeightPx / 2f) - roleSizePx, screenWidthPx.toFloat(), (screenHeightPx / 2f))) {
            message = "(碰撞兒童圖示)"
            resetService()
        }
        // Role 2: 成人 (Alignment.BottomStart)
        // Left: 0
        // Right: roleSizePx
        // Top: H - roleSizePx
        // Bottom: H
        else if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                0f, (screenHeightPx - roleSizePx), roleSizePx, screenHeightPx.toFloat())) {
            message = "(碰撞成人圖示)"
            resetService()
        }
        // Role 3: 一般民眾 (Alignment.BottomEnd)
        // Left: W - roleSizePx
        // Right: W
        // Top: H - roleSizePx
        // Bottom: H
        else if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                (screenWidthPx - roleSizePx), (screenHeightPx - roleSizePx), screenWidthPx.toFloat(), screenHeightPx.toFloat())) {
            message = "(碰撞一般民眾圖示)"
            resetService()
        }
    }

    private fun isOverlapping(l1: Float, t1: Float, r1: Float, b1: Float,
                              l2: Float, t2: Float, r2: Float, b2: Float): Boolean {
        return l1 < r2 && r1 > l2 && t1 < b2 && b1 > t2
    }
    
    fun updateServiceX(delta: Float) {
        serviceX += delta
    }
}