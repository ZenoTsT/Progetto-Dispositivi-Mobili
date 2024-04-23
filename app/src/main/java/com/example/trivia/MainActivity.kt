package com.example.trivia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val database by lazy {
        AppDatabase.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadQuestions()
    }

    private fun loadQuestions() {
        CoroutineScope(Dispatchers.IO).launch {
            val questions = database.questionDao().getAllQuestions()
            // Stampa le domande nel log
            questions.forEach { question ->
                Log.d("QuestionLog", "ID: ${question.id}, Domanda: ${question.question}, " +
                        "Risposta Corretta: ${question.correctAnswer}, Risposte Errate: ${question.incorrectAnswers}")
            }
        }
    }
}
