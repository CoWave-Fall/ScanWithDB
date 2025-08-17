package sys.tianr.test.scanwithdb.ui.about

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import sys.tianr.test.scanwithdb.databinding.FragmentAboutBinding
import sys.tianr.test.scanwithdb.R

data class OssLibrary(val name: String, val license: String)

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOssLicenseList()
        setupLogoVideo()
    }

    private fun setupLogoVideo() {
        val videoPath = "android.resource://" + requireActivity().packageName + "/" + R.raw.logo
        try {
            binding.authorLogoVideo.setVideoURI(Uri.parse(videoPath))

            // 监听视频准备好
            binding.authorLogoVideo.setOnPreparedListener { mp ->
                mp.isLooping = false // 确保不循环
                mp.start()
            }

            // 监听视频播放完成
            binding.authorLogoVideo.setOnCompletionListener { mp ->
                if (isAdded) {
                    binding.authorLogoVideo.setOnCompletionListener { mp ->
                        if (isAdded) {
                            val lastFrame = mp.duration - 1
                            if (lastFrame > 0) {
                                mp.seekTo(lastFrame)
                                // 发起seek后，短暂地start再立即pause，可以强制刷新画面
                                if (!mp.isPlaying) {
                                    mp.start()
                                    mp.pause()
                                }
                            }
                        }

                    }
                }
            }

        } catch (e: Exception) {
            // 视频未找到或其他错误
            binding.authorLogoVideo.visibility = View.GONE
        }
    }
    private fun setupOssLicenseList() {
        val libraries = listOf(
            OssLibrary("AndroidX Preference KTX", "Apache 2.0"),
            OssLibrary("AndroidX Core KTX", "Apache 2.0"),
            OssLibrary("AndroidX AppCompat", "Apache 2.0"),
            OssLibrary("Google Material Components", "Apache 2.0"),
            OssLibrary("AndroidX Activity KTX", "Apache 2.0"),
            OssLibrary("AndroidX ConstraintLayout", "Apache 2.0"),
            OssLibrary("AndroidX Navigation", "Apache 2.0"),
            OssLibrary("AndroidX Lifecycle", "Apache 2.0"),
            OssLibrary("AndroidX Room", "Apache 2.0"),
            OssLibrary("Google Hilt", "Apache 2.0"),
            OssLibrary("AndroidX CameraX", "Apache 2.0"),
            OssLibrary("Google ML Kit Barcode Scanning", "Apache 2.0"),
            OssLibrary("JUnit 4", "Eclipse Public License 1.0")
        )

        val adapter = OssLicenseAdapter(libraries)
        binding.ossLicensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ossLicensesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
