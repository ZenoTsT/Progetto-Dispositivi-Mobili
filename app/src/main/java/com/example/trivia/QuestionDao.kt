package com.example.trivia

import androidx.room.Dao
import androidx.room.Query

@Dao
interface QuestionDao {

    // Restituisce tutte le domande presenti nella tabella "questions" del database
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): List<Question>
}
