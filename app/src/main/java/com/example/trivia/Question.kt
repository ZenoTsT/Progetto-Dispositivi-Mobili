package com.example.trivia

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.regex.Pattern


@Entity(tableName = "questions")
class Question (
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "question")
    val question: String,

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String,

    @ColumnInfo(name = "incorrect_answers")
    val incorrectAnswers: String

)

{
    public fun getQuestionText(): String {
        return  question
    }

    public fun getCorrectAnswerText(): String {
        return  correctAnswer
    }

    public fun getIncorrectAnswersText(): ArrayList<String> {
        val regex = Pattern.compile("\"([^\"]*)\"")
        val matcher = regex.matcher(incorrectAnswers)
        val results = ArrayList<String>()

        while (matcher.find()) {
            matcher.group(1)?.let { results.add(it) }
        }

        return results
    }

    override fun toString(): String {
        return "ID: $id, question: $question, correct answer: $correctAnswer, incorrect answers: $incorrectAnswers"
    }

}