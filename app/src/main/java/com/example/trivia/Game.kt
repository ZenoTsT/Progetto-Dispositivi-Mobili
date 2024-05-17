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

    private suspend fun initQuestionList() = withContext(Dispatchers.IO) {
        try {
            Log.d("Game", "Loading questions from database")
            questionsList.addAll(AppDatabase.getInstance(applicationContext).questionDao().getAllQuestions())
            Log.d("Game", "Questions loaded successfully")
        } catch (e: Exception) {
            Log.e("Game", "Error loading data", e)
        }
    }




    private fun initPlayersList(players: ArrayList<String>){
        for(name in players){
            playersList.add(Player(name))
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: Game? = null

        fun getInstance(): Game {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Game().also {
                    INSTANCE = it
                }
            }
        }
    }

    fun setGame(context: Context, players: ArrayList<String>, onQuestionsLoaded: () -> Unit) {
        applicationContext = context

        CoroutineScope(Dispatchers.Main).launch {
            initQuestionList()
            onQuestionsLoaded()
        }

        initPlayersList(players)
    }

    public fun getQuestion(): Question? {

        if(questionsList.size == 0){
            return null
        }else{
            val randomIndex = Random.nextInt(questionsList.size)
            val randomElement = questionsList[randomIndex]
            questionsList.removeAt(randomIndex)
            return  randomElement
        }

    }

    public fun getCurrentPlayer(): Player {
        return playersList[turn]
    }

    public fun getCurrentTurn(): Int {
        return turn
    }

    public fun nextTurn() {
        turn = (turn + 1) % playersList.size
    }

    public fun getPlayerList(): ArrayList<Player> {
        return playersList
    }

    public fun getLeaderboard(): ArrayList<Player> {
        return ArrayList(playersList.sortedByDescending { it.getScore() })
    }


    public fun resetGame() {
        questionsList.clear()
        playersList.clear()
        turn = 0
    }

}
