package io.github.takusan23.smartphonekurukuru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Fragment設置
        supportFragmentManager.beginTransaction().replace(main_activity_parent_layout.id,KuruKuruFragment()).commit()

    }
}
