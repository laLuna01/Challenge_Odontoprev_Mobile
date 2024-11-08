package com.example.challengeodontoprev.model

data class User(
    var name: String,
    var dayOfBirth: String,
    var cpf: String,
    var endereco: String,
    var genero: String,
    var email: String,
    var password: String) {
    constructor() : this("", "",  "", "", "", "", "")
}