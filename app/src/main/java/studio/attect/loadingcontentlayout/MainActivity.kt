package studio.attect.loadingcontentlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnUFO.setOnClickListener {
            startActivity(Intent(this,UFOStyleActivity::class.java) )
        }
        btnHiyoko.setOnClickListener{
            startActivity(Intent(this,HiyokoStyleActivity::class.java))
        }
    }
}
