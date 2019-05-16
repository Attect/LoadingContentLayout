package studio.attect.loadingcontentlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.loadingContentLayout
import studio.attect.ui.FrameAnimation
import studio.attect.ui.LoadingContentLayout
import studio.attect.ui.LoadingContentUI

class MainActivity : AppCompatActivity(), LoadingContentUI {
    private val frameAnimation = FrameAnimation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnUFO.setOnClickListener {
            startActivity(Intent(this,UFOStyleActivity::class.java) )
        }
        btnHiyoko.setOnClickListener{
            startActivity(Intent(this,HiyokoStyleActivity::class.java))
        }
        btnTestBlur.setOnClickListener{
            loadingContentLayout?.showBlur(true,it)
            it.postDelayed({
                loadingContentLayout?.showBlur(false,it)
            },5000)
        }

    }

    override fun onStart() {
        super.onStart()

        frameAnimation.imageView = imageView
        frameAnimation.frameRess = intArrayOf(
            R.drawable.hiyoko_0000,
            R.drawable.hiyoko_0001,
            R.drawable.hiyoko_0002,
            R.drawable.hiyoko_0003,
            R.drawable.hiyoko_0004,
            R.drawable.hiyoko_0005,
            R.drawable.hiyoko_0006,
            R.drawable.hiyoko_0007
        )
        frameAnimation.duration = 1000 / 8
        frameAnimation.isRepeat = true
        frameAnimation.startPlay()
    }

    override fun onStop() {
        super.onStop()
        frameAnimation.release()
    }


    override fun getLoadContentLayout(): LoadingContentLayout? {
        return loadingContentLayout
    }
}
