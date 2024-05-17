package com.example.trivia

class Player(name: String) {

    private val name: String

    private var score: Int = 0

    init {
        this.name = name
    }

    // Aggiunge punti al punteggio del giocatore
    fun addPoints(pointsToAdd: Int) {
        this.score += pointsToAdd
    }

    // Restituisce il nome del giocatore
    fun getName(): String {
        return name
    }

    // Restituisce il punteggio del giocatore
    fun getScore(): Int {
        return score
    }
}
