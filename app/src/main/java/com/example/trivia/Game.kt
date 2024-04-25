package com.example.trivia

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Game(applicationContext: Context) {

    private val applicationContext: Context
    private val questionsList =  ArrayList<Question>()

    init {

        this.applicationContext = applicationContext
        CoroutineScope(Dispatchers.Main).launch {
            loadData()
        }

    }

    private suspend fun loadData() = withContext(Dispatchers.IO) {

        try {
            questionsList.addAll(AppDatabase.getInstance(applicationContext).questionDao().getAllQuestions())
            withContext(Dispatchers.Main) {
            }
        } catch (e: Exception) {
            Log.e("GameActivity", "Error loading data", e)
        }
    }


}