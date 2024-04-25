package com.example.trivia

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var playersContainer: LinearLayout
    private var playerCount = 1
    private val MAX_PLAYERS = 8 // Sostituisci con il numero massimo di giocatori che desideri

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
            val newPlayerInput = TextInputLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val newPlayerEditText = EditText(this).apply {
                hint = "Name"
                setTextColor(resources.getColor(R.color.dark_gray_text, null))
                setHintTextColor(resources.getColor(R.color.medium_gray, null))
            }

            newPlayerInput.addView(newPlayerEditText)
            playersContainer.addView(newPlayerInput)
            playerCount++
        } else {
            Toast.makeText(this, "Maximum number of players reached", Toast.LENGTH_SHORT).show()
        }
    }
}
