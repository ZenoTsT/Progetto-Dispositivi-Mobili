package com.example.trivia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var playersContainer: LinearLayout
    private var playerCount = 0
    private val MAX_PLAYERS = 8

    // Inizializza l'attività, imposta il listener dei pulsanti
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {} })

        playersContainer = findViewById(R.id.playersContainer)
        val buttonAddPlayer: ImageButton = findViewById(R.id.button_add_player)
        val buttonStart: Button = findViewById(R.id.btn_start)

        buttonAddPlayer.setOnClickListener {
            addPlayerField()
        }

        buttonStart.setOnClickListener {
            startGame()
        }
    }

    // Aggiunge un campo per l'inserimento del nome del giocatore se non è stato raggiunto il numero massimo di giocatori
    private fun addPlayerField() {
        if (playerCount < MAX_PLAYERS) {
            val playerView = LayoutInflater.from(this).inflate(R.layout.player_name_input, playersContainer, false)
            playersContainer.addView(playerView)
            playerCount++
        } else {
            Toast.makeText(this, "Maximum number of players reached", Toast.LENGTH_SHORT).show()
        }
    }

    // Avvia il gioco se sono presenti almeno tre giocatori con nomi univoci
    private fun startGame() {
        val playerNames: ArrayList<String> = extractPlayerNames()

        if (!hasMultiplePlayers(playerNames)) {
            Toast.makeText(this, "At least three players are needed to start the game", Toast.LENGTH_LONG).show()
        } else if (!areAllNamesUnique(playerNames)) {
            Toast.makeText(this, "Each player must have a unique name", Toast.LENGTH_LONG).show()
        } else {
            val game = Game.getInstance()
            game.setGame(this, extractPlayerNames()) {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Estrae i nomi dei giocatori dai campi di input
    private fun extractPlayerNames(): ArrayList<String> {
        val playerNames = ArrayList<String>()

        for (i in 0 until playersContainer.childCount) {
            val view = playersContainer.getChildAt(i)
            if (view is EditText) {
                val playerName = view.text.toString()
                if (playerName.isNotBlank()) {
                    playerNames.add(playerName)
                }
            }
        }
        return playerNames
    }

    // Controlla se tutti i nomi dei giocatori sono univoci
    private fun areAllNamesUnique(playerNames: ArrayList<String>): Boolean {
        val uniqueNames = HashSet<String>()
        for (name in playerNames) {
            if (!uniqueNames.add(name)) {
                return false
            }
        }
        return true
    }

    // Controlla se ci sono almeno tre giocatori
    private fun hasMultiplePlayers(playerNames: ArrayList<String>): Boolean {
        return playerNames.size > 2
    }
}
