package com.example.trivia

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import kotlin.random.Random

class BallMinigameActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var game: Game
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var ballImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var scoreTextView: TextView

    private var xPos = 0f
    private var yPos = 0f

    private lateinit var backboards: Array<ImageView>
    private var score = 0
    private var gameDuration = 6000L
    private lateinit var gameTimer: CountDownTimer

    // Inizializza i canestri, la posizione della palla e il sensor manager dell'accelerometro
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ball_minigame)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {} })

        game = Game.getInstance()

        ballImageView = findViewById(R.id.ballImageView)
        resultTextView = findViewById(R.id.resultTextView)
        scoreTextView = findViewById(R.id.scoreTextView)

        backboards = arrayOf(
            findViewById(R.id.backboard1),
            findViewById(R.id.backboard2),
            findViewById(R.id.backboard3)
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        ballImageView.post {
            xPos = ballImageView.x
            yPos = ballImageView.y
            positionBackboardsRandomly()
        }

        startGameTimer()
    }

    // Fa iniziare il countdown e chiama endGame quando finisce il timer
    private fun startGameTimer() {
        gameTimer = object : CountDownTimer(gameDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                scoreTextView.text = "Time left: $secondsRemaining s"
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameTimer.start()
    }

    // Gestisce la fine del gioco
    private fun endGame() {
        sensorManager.unregisterListener(this)

        if (score == 3) {
            val lastPlayer = game.getLeaderboard().size - 1
            game.getLeaderboard()[lastPlayer].addPoints(2)
            resultTextView.text = "You win!"
        } else {
            resultTextView.text = "You lose!"
        }

        resultTextView.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            returnToGameActivity()
        }, 2000)
    }

    // Necessarie al movimento della palla
    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        gameTimer.cancel()
    }

    // Ad ogni variazione del sensore cambia la posizione della palla e controlla le collisioni con i canestri
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = -event.values[0]
            val y = event.values[1]

            xPos += x * 2
            yPos += y * 2

            ballImageView.post {
                val parentWidth = (ballImageView.parent as View).width
                val parentHeight = (ballImageView.parent as View).height

                if (xPos < 0) xPos = 0f
                if (xPos > parentWidth - ballImageView.width) xPos = (parentWidth - ballImageView.width).toFloat()
                if (yPos < 0) yPos = 0f
                if (yPos > parentHeight - ballImageView.height) yPos = (parentHeight - ballImageView.height).toFloat()

                ballImageView.x = xPos
                ballImageView.y = yPos

                checkCollisions()
            }
        }
    }

    // Controlla le collisioni con i canestri e azzera il timer quando vengono colpiti tutti
    private fun checkCollisions() {
        for (backboard in backboards) {
            if (backboard.visibility == View.VISIBLE) {
                val ballRect = ballImageView.frameRect()
                val backboardRect = backboard.frameRectReduced(0.7f)

                if (ballRect.intersect(backboardRect)) {
                    backboard.visibility = View.GONE
                    score++

                    if (score == backboards.size) {
                        gameTimer.cancel()
                        gameTimer.onFinish()
                    }
                }
            }
        }
    }

    // Non utilizzata in questo contesto, ma deve essere implementata
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Torna alla GameActivity
    private fun returnToGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    // Ottiene il rettangolo di una vista
    private fun View.frameRect() = Rect().apply {
        getHitRect(this)
    }

    // Ottiene un rettangolo ridotto di una vista
    private fun View.frameRectReduced(factor: Float) = Rect().apply {
        getHitRect(this)
        val widthReduction = (width * (1 - factor) / 2).toInt()
        val heightReduction = (height * (1 - factor) / 2).toInt()
        inset(widthReduction, heightReduction)
    }

    // Posiziona i canestri in modo semi-casuale (un canestro sarà sempre nella metà superiore dello schermo e uno nella metà inferiore)
    // controllando che non si sovrappongano tra di loro e con la palla
    private fun positionBackboardsRandomly() {
        val parent = ballImageView.parent as RelativeLayout
        val parentWidth = parent.width
        val parentHeight = parent.height

        val occupiedRects = mutableListOf<Rect>()
        val midHeight = parentHeight / 2

        for ((index, backboard) in backboards.withIndex()) {
            var randomX: Int
            var randomY: Int
            var isOverlapping: Boolean
            var backboardRect: Rect

            do {
                if (index % 2 == 0) {
                    randomY = Random.nextInt(0, midHeight - backboard.height)
                } else {
                    randomY = Random.nextInt(midHeight, parentHeight - backboard.height)
                }
                randomX = Random.nextInt(0, parentWidth - backboard.width)

                backboardRect = Rect(randomX, randomY, randomX + backboard.width, randomY + backboard.height)

                val ballRect = ballImageView.frameRect()
                isOverlapping = Rect.intersects(backboardRect, ballRect) || occupiedRects.any { Rect.intersects(it, backboardRect) }
            } while (isOverlapping)

            backboard.x = randomX.toFloat()
            backboard.y = randomY.toFloat()
            backboard.visibility = View.VISIBLE

            occupiedRects.add(backboardRect)
        }
    }
}
