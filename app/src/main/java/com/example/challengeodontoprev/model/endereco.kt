package com.example.challengeodontoprev.model

data class Endereco(
    var cep: Long,
    var bairro: Long,
    var logradouro: String,
    var numero: Long,
    var referencia: String) {
    constructor() : this(0, 0, "", 0, "")
}

data class Bairro(
    var nome: String,
    var cidade: Long) {
    constructor() : this("", 0)
}

data class Cidade(
    var nome: String,
    var estado: Long) {
    constructor() : this("", 0)
}

data class Estado(
    var nome: String,
    var pais: Long) {
    constructor() : this("", 0)
}

data class Pais(
    var nome: String) {
    constructor() : this("")
}