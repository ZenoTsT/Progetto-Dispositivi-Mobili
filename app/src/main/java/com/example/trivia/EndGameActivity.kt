package com.example.trivia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EndGameActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var firstPlaceRankTextView: TextView
    private lateinit var firstPlaceNameTextView: TextView
    private lateinit var firstPlaceScoreTextView: TextView
    private lateinit var secondPlaceRankTextView: TextView
    private lateinit var secondPlaceNameTextView: TextView
    private lateinit var secondPlaceScoreTextView: TextView
    private lateinit var thirdPlaceRankTextView: TextView
    private lateinit var thirdPlaceNameTextView: TextView
    private lateinit var thirdPlaceScoreTextView: TextView
    private lateinit var leaderboardList: ListView
    private lateinit var homeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.endgame_activity)

        homeButton = findViewById(R.id.button_home)

        game = Game.getInstance()
        initViews()
        updatePodium()
        setupLeaderboard()
        homeButton.setOnClickListener {
            toHomePage()
        }
    }

    private fun initViews() {
        firstPlaceRankTextView = findViewById(R.id.rank_first)
        firstPlaceNameTextView = findViewById(R.id.player_name_first)
        firstPlaceScoreTextView = findViewById(R.id.player_score_first)
        secondPlaceRankTextView = findViewById(R.id.rank_second)
        secondPlaceNameTextView = findViewById(R.id.player_name_second)
        secondPlaceScoreTextView = findViewById(R.id.player_score_second)
        thirdPlaceRankTextView = findViewById(R.id.rank_third)
        thirdPlaceNameTextView = findViewById(R.id.player_name_third)
        thirdPlaceScoreTextView = findViewById(R.id.player_score_third)
        leaderboardList = findViewById(R.id.leaderboard_list)
    }

    private fun updatePodium() {
        val topPlayers = game.getLeaderboard().take(3)
        topPlayers.getOrNull(0)?.let {
            firstPlaceRankTextView.text = "1"
            firstPlaceNameTextView.text = it.getName()
            firstPlaceScoreTextView.text = "${it.getScore()} points"
        }
        topPlayers.getOrNull(1)?.let {
            secondPlaceRankTextView.text = "2"
            secondPlaceNameTextView.text = it.getName()
            secondPlaceScoreTextView.text = "${it.getScore()} points"
        }
        topPlayers.getOrNull(2)?.let {
            thirdPlaceRankTextView.text = "3"
            thirdPlaceNameTextView.text = it.getName()
            thirdPlaceScoreTextView.text = "${it.getScore()} points"
        }
    }

    private fun setupLeaderboard() {
        val remainingPlayers = game.getLeaderboard().drop(3)
        val adapter = LeaderboardAdapter(this, remainingPlayers, true)
        leaderboardList.adapter = adapter
    }

    private fun toHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
