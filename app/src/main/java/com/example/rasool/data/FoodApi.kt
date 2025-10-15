package com.example.rasool.data

import com.example.rasool.data.models.AuthResponse
import com.example.rasool.data.models.OAuthRequest
import com.example.rasool.data.models.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("/food")
    suspend fun getFood() : List<String>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest) : Response<AuthResponse>

    @POST("/auth/login")
    suspend fun signIn(@Body request: SignUpRequest) : AuthResponse
    @POST("/auth/oauth")
    fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>
}