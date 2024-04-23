package com.example.trivia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room

class MainActivity : AppCompatActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "Domande.db"
        )
            .createFromAsset("Domande.db")
            .allowMainThreadQueries() // Only for testing purposes!
            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*GlobalScope.launch(Dispatchers.IO) {
            val questions = database.questionDao().getAllQuestions()

            // Switch to the main thread to update UI
            withContext(Dispatchers.Main) {
                // Do something with the questions, e.g., display them in a RecyclerView or log to debug
                println("Fetched questions: $questions")
            }
        }*/
    }
}