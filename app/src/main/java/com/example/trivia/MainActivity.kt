package com.example.trivia

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var playersContainer: LinearLayout
    private var playerCount = 0
    private val MAX_PLAYERS = 8 // Substitute with the maximum number of players you want

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playersContainer = findViewById(R.id.playersContainer)
        val buttonAddPlayer: ImageButton = findViewById(R.id.button_add_player)

        buttonAddPlayer.setOnClickListener {
            addPlayerField()
        }
    }

    private fun addPlayerField() {
        if (playerCount < MAX_PLAYERS) {
            val playerView = LayoutInflater.from(this).inflate(R.layout.player_name_input, playersContainer, false)
            playersContainer.addView(playerView)
            playerCount++
        } else {
            Toast.makeText(this, "Maximum number of players reached", Toast.LENGTH_SHORT).show()
        }
    }
}
