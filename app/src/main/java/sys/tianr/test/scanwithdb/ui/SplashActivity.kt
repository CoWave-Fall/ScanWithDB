package sys.tianr.test.scanwithdb.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import sys.tianr.test.scanwithdb.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 应用启动屏 Activity。
 * 增加了视频播放功能，并使用 lifecycleScope 协程来处理延时跳转。
 *
 * @SuppressLint("CustomSplashScreen") 因为我们正在实现自定义的启动屏逻辑，
 * 所以需要禁用系统对不正确实现启动屏的警告。
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 初始化并播放 Logo 视频
        setupLogoVideo()

        // 启动一个与此 Activity 生命周期绑定的协程，用于延时跳转
        lifecycleScope.launch {
            // 延时，决定了启动屏显示的总时长
            delay(SPLASH_SCREEN_DELAY)

            // 延时结束后，创建意图以启动主界面
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // 结束当前的启动屏 Activity，这样用户按返回键时不会回到这里
            finish()
        }
    }

    /**
     * 设置并播放启动屏底部的 Logo 视频。
     * 视频播放完成后会暂停并停留在最后一帧。
     */
    private fun setupLogoVideo() {
        // 1. 找到 VideoView 控件
        val videoView = findViewById<VideoView>(R.id.splash_logo_video)

        // 2. 构建视频资源的路径
        // !! 重要：请确保你的视频文件名是 "logo"，并且放在 res/raw 目录下
        val videoPath = "android.resource://" + packageName + "/" + R.raw.logo

        try {
            videoView.setVideoURI(Uri.parse(videoPath))

            // 3. 监听视频准备完成事件
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = false // 关闭循环播放
                mp.start() // 准备好后立即开始播放
            }

            // 4. 监听视频播放完成事件
            videoView.setOnCompletionListener { mp ->
                // 使用我们之前验证成功的“备选方案”来停留到最后一帧
                val lastFrame = mp.duration - 1
                if (lastFrame > 0) {
                    mp.seekTo(lastFrame)
                    if (!mp.isPlaying) {
                        mp.start()
                        mp.pause()
                    }
                }
            }

        } catch (e: Exception) {
            // 如果视频文件未找到或发生其他错误，则隐藏 VideoView，防止显示黑块
            e.printStackTrace()
            videoView.visibility = View.GONE
        }
    }

    companion object {
        // 启动屏显示时长（毫秒）
        private const val SPLASH_SCREEN_DELAY = 1000L // 建议将时间延长一点，确保视频能播完
    }
}