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

    // 螢幕寬高 (由 View 層傳入)
    var screenWidthPx = 0
    var screenHeightPx = 0
    
    private var isInitialized = false

    fun initService(widthPx: Int, heightPx: Int) {
        if (!isInitialized) {
            screenWidthPx = widthPx
            screenHeightPx = heightPx
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
        serviceX = (screenWidthPx / 2).toFloat() // 初始水平置中 (需在 UI 層修正為中心點)
        serviceY = 0f
    }

    private fun startFalling() {
        viewModelScope.launch {
            while (true) {
                delay(100) // 每0.1秒
                serviceY += 20 // 往下掉20px

                // 碰撞檢測：如果超過螢幕高度 (這裡簡單假設圖示高度約 100-200px，觸底即重置)
                // 為了更精確，可以在 UI 層處理碰撞，或者這裡給個大概值
                if (serviceY > screenHeightPx) {
                    resetService()
                }
            }
        }
    }
    
    fun updateServiceX(delta: Float) {
        serviceX += delta
    }
}