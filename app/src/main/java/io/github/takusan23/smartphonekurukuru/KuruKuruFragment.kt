package io.github.takusan23.smartphonekurukuru

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_kurukuru.*
import kotlin.math.roundToInt

class KuruKuruFragment : Fragment() {

    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener

    var firstPos = 0f

    //加速度の値。配列になっている
    var accelerometerList = floatArrayOf()
    //磁気の値。こちらも配列になっている
    var magneticList = floatArrayOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kurukuru, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //加速度
        val accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
        //磁気
        val magnetic = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD)
        //受け取る
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //つかわん
            }

            override fun onSensorChanged(event: SensorEvent?) {
                //値はここで受けとる
                when (event?.sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        //加速度
                        accelerometerList = event.values.clone()
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        //地磁気
                        magneticList = event.values.clone()
                    }
                }
//配列に値があることを確認
                if (accelerometerList.isNotEmpty() && magneticList.isNotEmpty()) {
                    /*
                    * こっから先は何やってるかわからん！
                    * 回転行列ってなに？？？
                    * ここみて→https://developer.android.com/guide/topics/sensors/sensors_position
                    * */
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        accelerometerList,
                        magneticList
                    )
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    //画面回転は配列の３番目の値で-1か1のときに横になる。
                    val yokoTate = if (orientationAngles[2].roundToInt() >= 1) {
                        "よこ"
                    } else if (orientationAngles[2].roundToInt() <= -1) {
                        "よこ"
                    } else {
                        "たて"
                    }
                    /*
                    //画面回転する
                    when (orientationAngles[2].roundToInt()) {
                        -1 -> {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                        1 -> {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                        else -> {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                    */
                    if (firstPos == 0f) {
                        firstPos = String.format("%.1f",orientationAngles[0]).toFloat()
                    }

                    if (firstPos == String.format("%.1f",orientationAngles[0]).toFloat()) {
                        Toast.makeText(context,"一周しました",Toast.LENGTH_SHORT).show()
                    }

                    kurukuru_fragment_textview.text = """

    コンパス：${String.format("%.1f",orientationAngles[0])}
    地面に近づけると＋の値：${orientationAngles[1]}
    右か左へ傾けると値変わる：${orientationAngles[2]}

    端末の向きは：$yokoTate
""".trimIndent()

                }
            }
        }
        //加速度センサー登録
        sensorManager.registerListener(
            sensorEventListener,
            accelerometer[0],  //配列のいっこめ。
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )

        //磁気センサー登録
        sensorManager.registerListener(
            sensorEventListener,
            magnetic[0],  //配列のいっこめ。
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorEventListener)
    }
}