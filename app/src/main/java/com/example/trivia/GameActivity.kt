package com.example.trivia

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
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

        val endGameButton = findViewById<Button>(R.id.end_game_button)
        endGameButton.setOnClickListener {
            showEndGameConfirmation()
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
            loadPlayerTurn()
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
        game.nextTurn()
    }

    private fun openLeaderboard() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_leaderboard)

        val listView = dialog.findViewById<ListView>(R.id.leaderboard_list)
        val closeButton = dialog.findViewById<Button>(R.id.close_button)

        val leaderboard = game.getLeadearboard()
        val adapter = LeaderboardAdapter(this, leaderboard)
        listView.adapter = adapter

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showEndGameConfirmation() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.endgame_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            openEndGameActivity()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun openEndGameActivity() {
        // Here you could start a new activity or finish the current one
        val intent = Intent(this, EndGameActivity::class.java) // Assuming you have an EndGameActivity
        startActivity(intent)
        finish()
    }
}