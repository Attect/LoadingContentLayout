package studio.attect.loadingcontentlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ufo_style.*
import studio.attect.ui.LoadingContentLayout
import studio.attect.ui.LoadingContentUI

class UFOStyleActivity : AppCompatActivity(), LoadingContentUI {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ufo_style)
        textView.setOnClickListener {
            loadingContentLayout.startLoading()
            loadingContentLayout.postDelayed({
                if((Math.random() * 100 %2).toInt() == 1){
                    loadingContentLayout.stopLoading()
                }else{
                    loadingContentLayout.showFailed()
                }

            },4000)
        }

    }


    override fun getLoadContentLayout(): LoadingContentLayout? {
        return loadingContentLayout
    }
}

