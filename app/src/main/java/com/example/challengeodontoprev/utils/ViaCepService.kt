package com.example.challengeodontoprev.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {

    @GET("{cep}/json/")
    fun getEndereco(@Path("cep") cep: String): Call<ViaCepResponse>
}
