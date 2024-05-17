package com.example.trivia

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat

class MinigameIntroductionActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var introTextView: TextView
    private lateinit var playerTextView: TextView
    private lateinit var gotItButton: Button
    private lateinit var minigame: String

    // Inizializza l'attività, imposta il listener dei pulsanti
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame_introduction)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {} })

        game = Game.getInstance()

        minigame = intent.getStringExtra("minigameName") ?: ""

        introTextView = findViewById(R.id.introTextView)
        playerTextView = findViewById(R.id.playerTextView)
        gotItButton = findViewById(R.id.gotItButton)

        gotItButton.setOnClickListener {
            openMinigame()
        }

        setPlayer()

        when (minigame) {
            "compass" -> setCompassIntro()
            "ball" -> setBallIntro()
            "photo" -> setPhotoIntro()
        }
    }

    // Imposta il nome del giocatore corrente nella vista
    private fun setPlayer() {
        val lastPlayerIndex = game.getLeaderboard().size - 1
        val playerName = game.getLeaderboard()[lastPlayerIndex].getName()
        val text = "It's your turn: $playerName!"
        val spannable = SpannableString(text)

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.pure_black))
        val styleSpan = StyleSpan(Typeface.BOLD)

        val playerNameStart = "It's your turn: ".length
        spannable.setSpan(colorSpan, playerNameStart, playerNameStart + playerName.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannable.setSpan(styleSpan, playerNameStart, playerNameStart + playerName.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        playerTextView.text = spannable
        playerTextView.setTextColor(ContextCompat.getColor(applicationContext, R.color.medium_gray))
    }

    // Avvia il minigioco della bussola
    private fun setCompassMinigame() {
        val intent = Intent(this, CompassMinigameActivity::class.java)
        startActivity(intent)
    }

    // Avvia il minigioco della palla
    private fun setBallMinigame() {
        val intent = Intent(this, BallMinigameActivity::class.java)
        startActivity(intent)
    }

    // Avvia il minigioco della foto
    private fun setPhotoMinigame() {
        val intent = Intent(this, PhotoMinigameActivity::class.java)
        startActivity(intent)
    }

    // Imposta il testo delle istruzioni per il minigioco della bussola
    private fun setCompassIntro() {
        introTextView.text = "Rotate the phone to create the required angle between your arrow (green) and the north arrow (red)!"
    }

    // Imposta il testo delle istruzioni per il minigioco della palla
    private fun setBallIntro() {
        introTextView.text = "Tilt the phone to move the ball and hit the three baskets within the time limit!"
    }

    // Imposta il testo delle istruzioni per il minigioco della foto
    private fun setPhotoIntro() {
        introTextView.text = "Take a picture of an object in the required color!"
    }

    // Apre il minigioco scelto
    private fun openMinigame() {
        when (minigame) {
            "compass" -> setCompassMinigame()
            "ball" -> setBallMinigame()
            "photo" -> setPhotoMinigame()
        }
    }
}
