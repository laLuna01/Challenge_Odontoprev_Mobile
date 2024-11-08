package com.example.challengeodontoprev.model

data class Endereco(
    var cep: Long,
    var bairro: String,
    var logradouro: String,
    var numero: Long,
    var referencia: String) {
    constructor() : this(0, "", "", 0, "")
}