package com.example.trivia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class GameActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var textViewCurrentPlayer: TextView
    private lateinit var textViewQuestion: TextView
    private lateinit var buttonLeaderboard: ImageButton
    private lateinit var buttonAnswer1: Button
    private lateinit var buttonAnswer2: Button
    private lateinit var buttonAnswer3: Button
    private lateinit var buttonAnswer4: Button

    private var currentQuestion: Question? = null

    private var currentPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        game = Game.getInstance()

        textViewCurrentPlayer = findViewById(R.id.player_turn_text_view)
        textViewQuestion = findViewById(R.id.question_text_view)
        buttonLeaderboard = findViewById(R.id.leaderboard_button)
        buttonAnswer1 = findViewById(R.id.answer_button_one)
        buttonAnswer2 = findViewById(R.id.answer_button_two)
        buttonAnswer3 = findViewById(R.id.answer_button_three)
        buttonAnswer4 = findViewById(R.id.answer_button_four)

        buttonLeaderboard.setOnClickListener {
            openLeaderboard()
        }

        buttonAnswer1.setOnClickListener {
            checkAnswer(buttonAnswer1)
        }

        buttonAnswer2.setOnClickListener {
            checkAnswer(buttonAnswer2)
        }

        buttonAnswer3.setOnClickListener {
            checkAnswer(buttonAnswer3)
        }

        buttonAnswer4.setOnClickListener {
            checkAnswer(buttonAnswer4)
        }

        loadQuestion()
        loadPlayerTurn()

    }

    private fun checkAnswer(selectedButton: Button) {
        val correctAnswer = currentQuestion!!.getCorrectAnswerText()

        val isCorrect = selectedButton.text.toString() == correctAnswer

        if (isCorrect) {
            selectedButton.setBackgroundColor(getColor(R.color.correct_green))
            currentPlayer!!.addPoints(1)
        } else {
            val buttons = listOf(buttonAnswer1, buttonAnswer2, buttonAnswer3, buttonAnswer4)
            buttons.find { it.text.toString() == correctAnswer }?.setBackgroundColor(getColor(R.color.correct_green))
            selectedButton.setBackgroundColor(getColor(R.color.wrong_red))
        }

        game.nextTurn()

        Handler(Looper.getMainLooper()).postDelayed({
            loadQuestion()
            resetButtonColors()
        }, 1000)
    }

    private fun resetButtonColors() {
        val buttons = listOf(buttonAnswer1, buttonAnswer2, buttonAnswer3, buttonAnswer4)
        buttons.forEach { it.setBackgroundColor(getColor(R.color.lilla)) }
    }

    private fun loadQuestion() {
        currentQuestion = game.getQuestion()

        if(currentQuestion == null){
            //ENDGAME
        }else{
            textViewQuestion.text = currentQuestion!!.getQuestionText()
            val correctAnswer = currentQuestion!!.getCorrectAnswerText()
            val incorrectAnswers = currentQuestion!!.getIncorrectAnswersText()

            val allAnswers = ArrayList<String>()
            allAnswers.add(correctAnswer)
            allAnswers.addAll(incorrectAnswers)

            allAnswers.shuffle()

            buttonAnswer1.text = allAnswers[0]
            buttonAnswer2.text = allAnswers[1]
            buttonAnswer3.text = allAnswers[2]
            buttonAnswer4.text = allAnswers[3]
        }
    }

    private fun loadPlayerTurn(){
        currentPlayer = game.getCurrentPlayer()
        textViewCurrentPlayer.text = "It's your turn ${currentPlayer!!.getName()}"
    }

    private fun openLeaderboard() {
        var leaderBoardArrayList = game.getLeadearboard()
    }


}