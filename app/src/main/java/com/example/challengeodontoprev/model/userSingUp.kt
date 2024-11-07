package com.example.challengeodontoprev.model

data class User(
    var name: String,
    var dayOfBirth: String,
    var cpf: String,
    var endereco: Long,
    var genero: Long,
    var email: String,
    var password: String) {
    constructor() : this("", "",  "", 0, 0, "", "")
}