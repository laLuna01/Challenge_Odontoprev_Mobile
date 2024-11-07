package com.example.challengeodontoprev.model

data class User(
    var name: String,
    var lastName: String,
    var email: String,
    var password: String,
    var dayOfBirth: String) {
    constructor() : this("", "", "", "", "")
}