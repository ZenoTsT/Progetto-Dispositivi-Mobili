package com.example.trivia

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
import androidx.core.content.ContextCompat

class MinigameIntroductionActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var introTextView: TextView
    private lateinit var playerTextView: TextView
    private lateinit var gotItButton: Button
    private lateinit var minigame: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame_introduction)

        game = Game.getInstance()

        minigame = intent.getStringExtra("minigameName") ?: ""

        introTextView = findViewById(R.id.introTextView)
        playerTextView = findViewById(R.id.playerTextView)
        gotItButton = findViewById(R.id.gotItButton)

        gotItButton.setOnClickListener {
            openMinigame()
        }

        setPlayer()

        when(minigame){
            "compass" -> setCompassIntro()
            "ball" -> setBallIntro()
            "photo" -> setPhotoIntro()
        }
    }

    fun setPlayer(){
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

    fun setCompassMinigame(){
        val intent = Intent(this, CompassMinigameActivity::class.java)
        startActivity(intent)
    }

    fun setBallMinigame(){
        val intent = Intent(this, BallMinigameActivity::class.java)
        startActivity(intent)
    }

    fun setPhotoMinigame(){
        val intent = Intent(this, PhotoMinigameActivity::class.java)
        startActivity(intent)
    }

    fun setCompassIntro(){
        introTextView.text = "Rotate the phone to create the required angle between your arrow (green) and the north arrow (red)!"
    }

    fun setBallIntro(){
        introTextView.text = "Tilt the phone to move the ball and hit the three baskets within the time limit!"
    }


    fun setPhotoIntro(){
        introTextView.text = "Take a picture of an object in the required color!"
    }


    fun openMinigame(){
        when(minigame){
            "compass" -> setCompassMinigame()
            "ball" -> setBallMinigame()
            "photo" -> setPhotoMinigame()
        }
    }
}