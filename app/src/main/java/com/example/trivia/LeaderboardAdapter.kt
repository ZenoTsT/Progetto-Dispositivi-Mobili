package com.example.trivia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class LeaderboardAdapter(context: Context, players: List<Player>) : ArrayAdapter<Player>(context, 0, players) {

    private val goldColor = ContextCompat.getColor(context, R.color.gold)
    private val silverColor = ContextCompat.getColor(context, R.color.silver)
    private val bronzeColor = ContextCompat.getColor(context, R.color.bronze)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_leaderboard, parent, false)

        val currentPlayer = getItem(position)
        listItemView.findViewById<TextView>(R.id.rank).apply {
            text = "${position + 1}"
            setTextColor(when (position) {
                0 -> ContextCompat.getColor(context, R.color.gold)
                1 -> ContextCompat.getColor(context, R.color.silver)
                2 -> ContextCompat.getColor(context, R.color.bronze)
                else -> ContextCompat.getColor(context, R.color.medium_gray)
            })
        }

        listItemView.findViewById<TextView>(R.id.player_name).text = currentPlayer?.getName()
        listItemView.findViewById<TextView>(R.id.player_score).text = "${currentPlayer?.getScore()} punti"

        return listItemView
    }
}
