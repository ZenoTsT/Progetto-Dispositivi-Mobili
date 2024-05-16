package com.example.trivia

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.round

class CompassMinigameActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var game: Game
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private var currentOrientation = FloatArray(3)
    private var currentAzimuth = 0.0f

    private lateinit var instructionTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var confirmButton: Button
    private lateinit var northArrow: ImageView
    private lateinit var playerArrow: ImageView

    private var targetAngle = 0.0f
    private var tolerance = 10.0f

    private var direction = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass_minigame)

        game = Game.getInstance()

        instructionTextView = findViewById(R.id.instructionTextView)
        resultTextView = findViewById(R.id.resultTextView)
        confirmButton = findViewById(R.id.confirmButton)
        northArrow = findViewById(R.id.northArrow)
        playerArrow = findViewById(R.id.playerArrow)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        generateNewInstruction()

        confirmButton.setOnClickListener {
            checkResult()
        }
    }

    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, currentOrientation)

            currentAzimuth = Math.toDegrees(currentOrientation[0].toDouble()).toFloat()
            if (currentAzimuth < 0) {
                currentAzimuth += 360
            }

            currentAzimuth = currentAzimuth % 360

            // Round azimuth to nearest integer
            val roundedAzimuth = round(currentAzimuth).toInt()

            // Update north arrow rotation
            northArrow.rotation = -roundedAzimuth.toFloat()

            // Player arrow should remain pointing up (0 degrees rotation)
            playerArrow.rotation = 0f
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement if needed
    }

    private fun generateNewInstruction() {

        currentAzimuth = 0.0f

        val isClockwise = (0..100).random()
        direction = if (isClockwise < 50) "clockwise" else "counter clockwise"
        val angle = (20..340).random()
        instructionTextView.text = "Turn $angle°\n$direction"

        if (direction == "clockwise") {
            targetAngle = (currentAzimuth + angle) % 360
        } else {
            targetAngle = (currentAzimuth - angle + 360) % 360
        }

        if (targetAngle < 0) {
            targetAngle += 360
        }
        targetAngle = targetAngle % 360

        targetAngle = round(targetAngle).toInt().toFloat()
    }

    private fun checkResult() {
        val roundedAzimuth = round(currentAzimuth).toInt()
        val roundedTargetAngle = round(targetAngle).toInt()

        confirmButton.isEnabled = false

        val angleDifference = abs(roundedAzimuth - roundedTargetAngle)
        if (angleDifference <= tolerance || abs(angleDifference - 360) <= tolerance) {
            resultTextView.text = "You win!"
            val lastPlayer = game.getLeaderboard().size - 1
            game.getLeaderboard()[lastPlayer].addPoints(2)

        } else {
            resultTextView.text = "You lose"
        }

        Handler(Looper.getMainLooper()).postDelayed({
            returnToGameActivity()
        }, 2000)

    }

    private fun returnToGameActivity(){
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

}