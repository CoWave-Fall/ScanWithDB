package sys.tianr.test.scanwithdb.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import sys.tianr.test.scanwithdb.R
import sys.tianr.test.scanwithdb.databinding.ActivitySplashBinding
import sys.tianr.test.scanwithdb.ui.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the activity edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Placeholder for the author's logo video
        val videoPath = "android.resource://" + packageName + "/" + R.raw.logo
        try {
            binding.splashLogoVideo.setVideoURI(Uri.parse(videoPath))
            binding.splashLogoVideo.setOnCompletionListener {
                // Freeze on the last frame
                binding.splashLogoVideo.seekTo(binding.splashLogoVideo.duration - 1)
            }
            binding.splashLogoVideo.start()
        } catch (e: Exception) {
            // Video not found, just proceed
        }

        // Navigate to MainActivity after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000) // 2 seconds delay
    }
}
