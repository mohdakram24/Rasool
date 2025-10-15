package com.example.rasool.ui.features.auth

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rasool.data.FoodApi
import com.example.rasool.data.auth.GoogleAuthUiProvider
import com.example.rasool.data.models.AuthResponse
import com.example.rasool.data.models.OAuthRequest
import com.example.rasool.data.remote.ApiResponse
import com.example.rasool.data.remote.safeApiCall
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi): ViewModel() {
    var error = ""
    var errorDescription = ""
    abstract fun loading()
    abstract fun onGoogleError(msg: String)
    abstract fun onSocialLoginSuccess(token: String)
    val googleAuthUiProvider = GoogleAuthUiProvider()
    protected fun initiateGoogleLogin(foodApi: FoodApi, context: ComponentActivity){
        viewModelScope.launch {
            try {
                val response = googleAuthUiProvider.signIn(
                    context,
                    CredentialManager.create(context = context)
                )
                fetchFoodAppToken(response.token, "google"){
                    onGoogleError(it)
                }
            }
            catch (ex: Throwable){
                onGoogleError(ex.message.toString())
            }

        }
    }
    fun fetchFoodAppToken(token: String, provider: String, onError: (String) -> Unit){
        viewModelScope.launch {
            val request = OAuthRequest(
                token = token,
                provider = provider
            )
            val res = safeApiCall<AuthResponse>{ foodApi.oAuth(request = request)}
            when(res){
                is ApiResponse.Success -> {
                    onSocialLoginSuccess(res.data.token)
                }
                else -> {
                    val error = (res as? ApiResponse.Error)?.code
                    if(error != null){
                        when(error){
                            401 -> "Invalid Token"
                            else -> {
                                onGoogleError("Failed")
                            }
                        }
                    }
                    else{
                        onGoogleError("Failed")
                    }

                }
            }
        }
    }
}