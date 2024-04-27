package com.example.trivia

class Player (name: String){

    private val name: String

    private var score: Int = 0

    init {
        this.name = name
    }

    public fun addPoints (pointsToAdd: Int){
        this.score = this.score + pointsToAdd
    }

    public fun getName(): String {
        return name
    }

    public fun getScore(): Int {
        return score
    }

}