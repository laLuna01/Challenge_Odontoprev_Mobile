package com.example.challengeodontoprev.model

data class UserSingUp(
    var name: String,
    var email: String,
    var password: String) {
    constructor() : this("", "", "")
}