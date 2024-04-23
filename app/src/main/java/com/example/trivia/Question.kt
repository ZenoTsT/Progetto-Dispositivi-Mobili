package com.example.trivia

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "questions")
data class Question (
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "question")
    val question: String,

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String,

    @ColumnInfo(name = "incorrect_answers")
    val incorrectAnswers: String
)