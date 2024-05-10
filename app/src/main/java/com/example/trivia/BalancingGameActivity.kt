package com.example.trivia

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class BalancingGameActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var ballImageView: ImageView
    private lateinit var scoreTextView: TextView

    private var xPos = 0f
    private var yPos = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balancing_game)

        ballImageView = findViewById(R.id.ballImageView)
        scoreTextView = findViewById(R.id.scoreTextView)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Inizializza la posizione centrale della pallina
        ballImageView.post {
            xPos = ballImageView.x
            yPos = ballImageView.y
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = -event.values[0]
            val y = event.values[1]

            // Sposta la pallina in base ai valori dell'accelerometro
            xPos += x * 2
            yPos += y * 2

            // Assicurati di avere le dimensioni corrette del parent
            ballImageView.post {
                val parentWidth = (ballImageView.parent as View).width
                val parentHeight = (ballImageView.parent as View).height

                // Impedisci alla pallina di uscire dai bordi
                if (xPos < 0) xPos = 0f
                if (xPos > parentWidth - ballImageView.width) xPos = (parentWidth - ballImageView.width).toFloat()
                if (yPos < 0) yPos = 0f
                if (yPos > parentHeight - ballImageView.height) yPos = (parentHeight - ballImageView.height).toFloat()

                // Imposta la posizione della pallina
                ballImageView.x = xPos
                ballImageView.y = yPos
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implementare se necessario
    }
}
