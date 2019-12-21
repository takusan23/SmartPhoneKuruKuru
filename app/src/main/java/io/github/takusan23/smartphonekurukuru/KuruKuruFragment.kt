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

    var check_3 = false
    var check_2 = false
    var check_1 = false
    var check_0 = false

    var check_minus_3 = false
    var check_minus_2 = false
    var check_minus_1 = false
    var check_minus_0 = false

    var rotateCount = 0
    var count = 0

    var calc = 0

    val list = arrayListOf<Int>()

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
                        firstPos = String.format("%.1f", orientationAngles[0]).toFloat()
                    }

                    val formatFloat = orientationAngles[0].toInt()


/*
                    if (formatFloat == 3) {
                        check_3 = true
                    }
                    if (formatFloat == 2) {
                        check_2 = true
                    }
                    if (formatFloat == 1) {
                        check_1 = true
                    }
                    if (formatFloat == 0) {
                        check_0 = true
                    }
                    if (formatFloat == -3) {
                        check_minus_3 = true
                    }
                    if (formatFloat == -2) {
                        check_minus_2 = true
                    }
                    if (formatFloat == -1) {
                        check_minus_1 = true
                    }
                    if (formatFloat == -0) {
                        check_minus_0 = true
                    }
*/

                    // accelerometer_textview.text = "${orientationAngles[0].toInt()}"

                    if (formatFloat == -2 || formatFloat == -1) {
                        check_minus_1 = true
                    }
                    if (formatFloat == 2 || formatFloat == 1) {
                        check_1 = true
                    }

                    if (formatFloat == 3 && !list.contains(3)) {
                        list.add(3)
                    }
                    if (formatFloat == 2 && !list.contains(2)) {
                        list.add(2)
                    }
                    if (formatFloat == 1 && !list.contains(1)) {
                        list.add(1)
                    }
                    if (formatFloat == 0 && !list.contains(0)) {
                        list.add(0)
                    }
                    if (formatFloat == -3 && !list.contains(-3)) {
                        list.add(-3)
                    }
                    if (formatFloat == -2 && !list.contains(-2)) {
                        list.add(-2)
                    }
                    if (formatFloat == -1 && !list.contains(-1)) {
                        list.add(-1)
                    }
                    if (formatFloat == -0 && !list.contains(-0)) {
                        list.add(-0)
                    }
                    accelerometer_textview.text = "${list}"

                    println(list)

                    if (list.size >= 5) {

                        println("回転しました")
                        check_3 = false
                        check_2 = false
                        check_1 = false
                        check_0 = false
                        check_minus_3 = false
                        check_minus_2 = false
                        check_minus_1 = false
                        check_minus_0 = false

                        list.clear()
//
                        count++
                        kurukuru_count_textview.text = "$count 回"
                        // if (count % 2 == 0) {
                        //     rotateCount++
                        //     kurukuru_count_textview.text = "$rotateCount 回"
                        // }
                    }

                    kurukuru_fragment_textview.text = """
                        
                        3 $check_3
                        2 $check_2
                        1 $check_1
                        0 $check_0
                        
                        -3 $check_minus_3
                        -2 $check_minus_2
                        -1 $check_minus_1
                        -0 $check_minus_0

                    """.trimIndent()


                }
            }
        }
        //加速度センサー登録
        sensorManager.registerListener(
            sensorEventListener,
            accelerometer[0],  //配列のいっこめ。
            SensorManager.SENSOR_DELAY_FASTEST  //更新頻度
        )

        //磁気センサー登録
        sensorManager.registerListener(
            sensorEventListener,
            magnetic[0],  //配列のいっこめ。
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )

        button.setOnClickListener {
            check_3 = false
            check_2 = false
            check_1 = false
            check_0 = false
            check_minus_3 = false
            check_minus_2 = false
            check_minus_1 = false
            check_minus_0 = false
            kurukuru_count_textview.text = "回転数"
            count = 0
            rotateCount = 0
            list.clear()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorEventListener)
    }
}