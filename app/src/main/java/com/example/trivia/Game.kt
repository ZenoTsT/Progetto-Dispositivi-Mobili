package com.example.trivia

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Game private constructor() {

    private lateinit var applicationContext: Context

    private val questionsList = ArrayList<Question>()

    private val playersList = ArrayList<Player>()

    private suspend fun initQuestionList() = withContext(Dispatchers.IO) {
        try {
            questionsList.addAll(AppDatabase.getInstance(applicationContext).questionDao().getAllQuestions())
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

    public fun setGame(context: Context, players: ArrayList<String>){

        applicationContext = context

        CoroutineScope(Dispatchers.Main).launch {
            initQuestionList()
        }

        initPlayersList(players)

    }

}
