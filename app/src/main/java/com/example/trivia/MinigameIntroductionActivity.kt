package com.example.trivia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MinigameIntroductionActivity : AppCompatActivity() {

    private lateinit var introTextView: TextView
    private lateinit var gotItButton: Button
    private lateinit var minigame: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame_introduction)

        minigame = intent.getStringExtra("minigameName") ?: ""

        introTextView = findViewById(R.id.introTextView)
        gotItButton = findViewById(R.id.gotItButton)

        gotItButton.setOnClickListener {
            openMinigame()
        }

        when(minigame){
            "compass" -> setCompassIntro()
            "ball" -> setBallIntro()
        }
    }

    fun setCompassMinigame(){
        val intent = Intent(this, CompassMinigameActivity::class.java)
        startActivity(intent)
    }

    fun setCompassIntro(){
        introTextView.text = "Rotate the phone to create the required angle between your arrow (green) and the north arrow (red)!"
    }

    fun setBallMinigame(){
        val intent = Intent(this, BallMinigameActivity::class.java)
        startActivity(intent)
    }

    fun setBallIntro(){
        introTextView.text = "Tilt the phone to move the ball and hit the three baskets within the time limit!"
    }

    fun openMinigame(){
        when(minigame){
            "compass" -> setCompassMinigame()
            "ball" -> setBallMinigame()
        }
    }
}