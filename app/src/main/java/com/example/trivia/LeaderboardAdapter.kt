package com.example.trivia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class LeaderboardAdapter(context: Context, players: List<Player>, private val isEndGame: Boolean) : ArrayAdapter<Player>(context, 0, players) {

    // Restituisce una vista per un elemento della classifica
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_leaderboard, parent, false)

        val currentPlayer = getItem(position)
        listItemView.findViewById<TextView>(R.id.rank).apply {

            var truePosition = 0
            if (!isEndGame) {
                truePosition = position + 1
            } else {
                truePosition = position + 4
            }

            text = "${truePosition}"

            setTextColor(when (truePosition) {
                1 -> ContextCompat.getColor(context, R.color.gold)
                2 -> ContextCompat.getColor(context, R.color.silver)
                3 -> ContextCompat.getColor(context, R.color.bronze)
                else -> ContextCompat.getColor(context, R.color.medium_gray)
            })
        }

        listItemView.findViewById<TextView>(R.id.player_name).text = currentPlayer?.getName()
        listItemView.findViewById<TextView>(R.id.player_score).text = "${currentPlayer?.getScore()} points"

        return listItemView
    }
}
