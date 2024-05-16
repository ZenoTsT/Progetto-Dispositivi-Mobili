package com.example.trivia

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
import android.widget.Toast
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
    private var gameDuration = 7000L // Durata del gioco in millisecondi (30 secondi)
    private lateinit var gameTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ball_minigame)

        game = Game.getInstance()

        ballImageView = findViewById(R.id.ballImageView)
        resultTextView = findViewById(R.id.resultTextView)
        scoreTextView = findViewById(R.id.scoreTextView)

        // Inizializza i canestri
        backboards = arrayOf(
            findViewById(R.id.backboard1),
            findViewById(R.id.backboard2),
            findViewById(R.id.backboard3)
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Inizializza la posizione centrale della pallina
        ballImageView.post {
            xPos = ballImageView.x
            yPos = ballImageView.y
            positionBackboardsRandomly() // Posiziona i canestri casualmente
        }

        // Avvia il timer del gioco
        startGameTimer()
    }

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

    private fun endGame() {
        sensorManager.unregisterListener(this)

        if (score == 3) {
            val lastPlayer = game.getLeaderboard().size - 1
            game.getLeaderboard()[lastPlayer].addPoints(2)
            resultTextView.text = "You win!"
        } else {
            resultTextView.text = "You lose!"
        }

        resultTextView.visibility = View.VISIBLE // Rendi visibile il TextView

        Handler(Looper.getMainLooper()).postDelayed({
            returnToGameActivity()
        }, 2000)
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
        gameTimer.cancel()
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

                // Controlla le collisioni con i canestri
                checkCollisions()
            }
        }
    }

    private fun checkCollisions() {
        for (backboard in backboards) {
            if (backboard.visibility == View.VISIBLE) {
                val ballRect = ballImageView.frameRect()
                val backboardRect = backboard.frameRectReduced(0.7f) // Riduce la dimensione della scatola di collisione

                if (ballRect.intersect(backboardRect)) {
                    backboard.visibility = View.GONE
                    score++

                    // Verifica se tutti i canestri sono stati presi
                    if (score == backboards.size) {
                        gameTimer.cancel()  // Annulla il timer
                        gameTimer.onFinish() // Chiama onFinish per terminare il gioco immediatamente
                    }
                }
            }
        }
    }


    private fun View.frameRect() = Rect().apply {
        getHitRect(this)
    }

    private fun View.frameRectReduced(factor: Float) = Rect().apply {
        getHitRect(this)
        val widthReduction = (width * (1 - factor) / 2).toInt()
        val heightReduction = (height * (1 - factor) / 2).toInt()
        inset(widthReduction, heightReduction)
    }

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
                // Genera posizioni per metà verticale dello schermo
                if (index % 2 == 0) {
                    // Metà superiore dello schermo
                    randomY = Random.nextInt(0, midHeight - backboard.height)
                } else {
                    // Metà inferiore dello schermo
                    randomY = Random.nextInt(midHeight, parentHeight - backboard.height)
                }
                randomX = Random.nextInt(0, parentWidth - backboard.width)

                // Crea il rettangolo del backboard per controllare l'overlap
                backboardRect = Rect(randomX, randomY, randomX + backboard.width, randomY + backboard.height)

                // Verifica se si sovrappone alla palla o ad altri backboard
                val ballRect = ballImageView.frameRect()
                isOverlapping = Rect.intersects(backboardRect, ballRect) || occupiedRects.any { Rect.intersects(it, backboardRect) }
            } while (isOverlapping)

            backboard.x = randomX.toFloat()
            backboard.y = randomY.toFloat()
            backboard.visibility = View.VISIBLE

            // Aggiungi il rettangolo del backboard alla lista degli occupati
            occupiedRects.add(backboardRect)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implementare se necessario
    }

    private fun returnToGameActivity(){
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

}
