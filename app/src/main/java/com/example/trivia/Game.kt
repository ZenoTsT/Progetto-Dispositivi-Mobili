package com.example.trivia

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class Game private constructor() {

    private lateinit var applicationContext: Context

    private val questionsList = ArrayList<Question>()

    private val playersList = ArrayList<Player>()

    private var turn = 0

    // Carica la lista di domande dal database tramite una coroutine IO
    private suspend fun initQuestionList() = withContext(Dispatchers.IO) {
        try {
            Log.d("Game", "Loading questions from database")
            questionsList.addAll(AppDatabase.getInstance(applicationContext).questionDao().getAllQuestions())
            Log.d("Game", "Questions loaded successfully")
        } catch (e: Exception) {
            Log.e("Game", "Error loading data", e)
        }
    }

    // Inizializza la lista dei giocatori con i nomi forniti
    private fun initPlayersList(players: ArrayList<String>) {
        for (name in players) {
            playersList.add(Player(name))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Game? = null

        // Restituisce l'istanza singleton di Game, creandone una nuova se non esiste già
        fun getInstance(): Game {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Game().also {
                    INSTANCE = it
                }
            }
        }
    }

    // Configura il gioco con il contesto dell'applicazione, la lista dei giocatori e una callback da chiamare dopo il caricamento delle domande
    fun setGame(context: Context, players: ArrayList<String>, onQuestionsLoaded: () -> Unit) {
        applicationContext = context

        CoroutineScope(Dispatchers.Main).launch {
            initQuestionList()
            onQuestionsLoaded()
        }

        initPlayersList(players)
    }

    // Restituisce una domanda casuale rimuovendola dalla lista
    fun getQuestion(): Question? {
        return if (questionsList.isEmpty()) {
            null
        } else {
            val randomIndex = Random.nextInt(questionsList.size)
            val randomElement = questionsList[randomIndex]
            questionsList.removeAt(randomIndex)
            randomElement
        }
    }

    // Restituisce il giocatore corrente
    fun getCurrentPlayer(): Player {
        return playersList[turn]
    }

    // Restituisce il turno corrente
    fun getCurrentTurn(): Int {
        return turn
    }

    // Passa al turno successivo
    fun nextTurn() {
        turn = (turn + 1) % playersList.size
    }

    // Restituisce la lista dei giocatori
    fun getPlayerList(): ArrayList<Player> {
        return playersList
    }

    // Restituisce la classifica dei giocatori ordinata per punteggio decrescente
    fun getLeaderboard(): ArrayList<Player> {
        return ArrayList(playersList.sortedByDescending { it.getScore() })
    }

    // Reimposta il gioco, cancellando domande, giocatori e azzerando il turno
    fun resetGame() {
        questionsList.clear()
        playersList.clear()
        turn = 0
    }
}
