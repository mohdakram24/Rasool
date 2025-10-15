package com.example.rasool.ui.features.auth.signup

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.example.rasool.data.FoodApi
import com.example.rasool.data.models.SignUpRequest
import com.example.rasool.data.remote.ApiResponse
import com.example.rasool.data.remote.safeApiCall
import com.example.rasool.ui.features.auth.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(override val foodApi: FoodApi) : BaseAuthViewModel(foodApi = foodApi) {


    //Holds the previous value
    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    // Don't hold the previous value
    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _name = MutableStateFlow("")
    val name = _name
    private val _email = MutableStateFlow("")
    val email = _email
    private val _password = MutableStateFlow("")
    val password = _password

    fun onNameChange(name: String){
        _name.value = name
    }
    fun onEmailChange(email: String){
        _email.value = email
    }
    fun onPasswordChange(password: String){
        _password.value = password
    }
    fun onLoginClicked(){
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToLogin)
        }
    }

    fun onGoogleSignInClicked(context: Context){
        initiateGoogleLogin(foodApi = foodApi, context = context as ComponentActivity)
    }

    fun onSignUpClick(){
        viewModelScope.launch {
            try{
                _uiState.value = SignUpEvent.Loading
                if(_name.value.isEmpty()){
                    error = "Full Name"
                    errorDescription = "Please enter name"
                    _navigationEvent.emit(SignUpNavigationEvent.ShowError)
                    _uiState.value = SignUpEvent.Nothing
                    return@launch
                }
                if(_email.value.isEmpty()){
                    error = "Email"
                    errorDescription = "Please enter Email"
                    _navigationEvent.emit(SignUpNavigationEvent.ShowError)
                    _uiState.value = SignUpEvent.Nothing
                    return@launch
                }
                if(_password.value.isEmpty()){
                    error = "Password"
                    errorDescription = "Please enter Password"
                    _navigationEvent.emit(SignUpNavigationEvent.ShowError)
                    _uiState.value = SignUpEvent.Nothing
                    return@launch
                }
                val response = foodApi.signUp(
                    SignUpRequest(
                        name = name.value,
                        email = email.value,
                        password = password.value
                    )
                )
                if(response.body()?.token?.isNotEmpty() == true){
                    _uiState.value = SignUpEvent.Success
                    _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
                }
            }
            catch (ex: Exception){
                ex.printStackTrace()
                _uiState.value = SignUpEvent.Error
            }
        }
    }

    override fun loading() {
        _uiState.value = SignUpEvent.Loading
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Error
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Success
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
        }
    }

    sealed class SignUpNavigationEvent{
        object NavigateToLogin : SignUpNavigationEvent()
        object NavigateToHome : SignUpNavigationEvent()
        object ShowError : SignUpNavigationEvent()
    }
    sealed class SignUpEvent{
        object Nothing: SignUpEvent()
        object Success: SignUpEvent()
        object Error: SignUpEvent()
        object Loading: SignUpEvent()
    }
}