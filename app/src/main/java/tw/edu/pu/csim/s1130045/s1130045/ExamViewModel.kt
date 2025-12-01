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
    
    // 分數與 Toast 訊息
    var score by mutableIntStateOf(0)
    var toastMessage by mutableStateOf<String?>(null)

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
        message = "" // 清除上一題的訊息
    }

    private fun startFalling() {
        viewModelScope.launch {
            while (true) {
                delay(100) // 每0.1秒
                serviceY += 20 // 往下掉20px
                
                val collidedRole = checkCollisionAndGetRole()
                var roundEnded = false

                if (collidedRole != -1) {
                    // 發生碰撞
                    handleRoundResult(collidedRole)
                    roundEnded = true
                } else if (serviceY > screenHeightPx) {
                    // 掉到最下方
                    handleRoundResult(-1) 
                    roundEnded = true
                }
                
                if (roundEnded) {
                    delay(3000) // 暫停3秒
                    resetService()
                }
            }
        }
    }
    
    private fun handleRoundResult(collidedRole: Int) {
        val correctRole = getCorrectRole(currentServiceId)
        val answerText = getAnswerText(currentServiceId)
        
        if (collidedRole == -1) {
            // 掉到最下方 (視為錯誤，扣分)
            score -= 1
            message = "掉到最下方"
        } else {
            if (collidedRole == correctRole) {
                score += 1
                message = "答對了！"
            } else {
                score -= 1
                message = "答錯了！"
            }
        }
        
        // 設定 Toast 訊息
        toastMessage = answerText
    }
    
    private fun getCorrectRole(serviceIdRes: Int): Int {
         return when (serviceIdRes) {
            R.drawable.service0 -> 0 // 嬰幼兒
            R.drawable.service1 -> 1 // 兒童
            R.drawable.service2 -> 2 // 成人
            else -> 3 // 一般民眾
        }
    }
    
    private fun getAnswerText(serviceIdRes: Int): String {
        return when (serviceIdRes) {
            R.drawable.service0 -> "極早期療育屬於嬰幼兒方面的服務"
            R.drawable.service1 -> "離島服務屬於兒童方面的服務"
            R.drawable.service2 -> "極重多障屬於成人方面的服務"
            else -> "輔具服務屬於一般民眾方面的服務"
        }
    }

    private fun checkCollisionAndGetRole(): Int {
        // 角色大小 300px (基於 UI 設定)
        val roleSizePx = 300f 
        
        // 服務圖示大小 (ExamScreen 中是 50.dp)
        val serviceSizePx = 50 * density
        
        // 服務圖示 Rect (Left, Top, Right, Bottom)
        // UI offset: x = serviceX - serviceSizePx/2 (置中), y = serviceY
        val serviceLeft = serviceX - serviceSizePx / 2
        val serviceTop = serviceY
        val serviceRight = serviceLeft + serviceSizePx
        val serviceBottom = serviceTop + serviceSizePx

        // 檢查與四個角色的碰撞
        // Role 0: 嬰幼兒
        if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                0f, (screenHeightPx / 2f) - roleSizePx, roleSizePx, (screenHeightPx / 2f))) {
            return 0
        }
        // Role 1: 兒童
        else if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                (screenWidthPx - roleSizePx), (screenHeightPx / 2f) - roleSizePx, screenWidthPx.toFloat(), (screenHeightPx / 2f))) {
            return 1
        }
        // Role 2: 成人
        else if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                0f, (screenHeightPx - roleSizePx), roleSizePx, screenHeightPx.toFloat())) {
            return 2
        }
        // Role 3: 一般民眾
        else if (isOverlapping(serviceLeft, serviceTop, serviceRight, serviceBottom,
                (screenWidthPx - roleSizePx), (screenHeightPx - roleSizePx), screenWidthPx.toFloat(), screenHeightPx.toFloat())) {
            return 3
        }
        
        return -1 // 未碰撞
    }

    private fun isOverlapping(l1: Float, t1: Float, r1: Float, b1: Float,
                              l2: Float, t2: Float, r2: Float, b2: Float): Boolean {
        return l1 < r2 && r1 > l2 && t1 < b2 && b1 > t2
    }
    
    fun updateServiceX(delta: Float) {
        serviceX += delta
    }
    
    fun clearToastMessage() {
        toastMessage = null
    }
}