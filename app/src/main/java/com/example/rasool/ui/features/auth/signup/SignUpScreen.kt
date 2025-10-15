package com.example.rasool.ui.features.auth.signup

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rasool.R
import com.example.rasool.ui.BasicDialog
import com.example.rasool.ui.FoodHubTextField
import com.example.rasool.ui.GroupSocialButtons
import com.example.rasool.ui.navigation.NavRoutes
import com.example.rasool.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {

    val viewModel: SignUpViewModel = hiltViewModel()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    when(uiState){
        SignUpViewModel.SignUpEvent.Error -> {
            loading.value = false
            errorMessage.value = "FAILED"
        }
        SignUpViewModel.SignUpEvent.Loading -> {
            loading.value = true
            errorMessage.value = null
        }
        else -> {
            loading.value = false
            errorMessage.value = null
        }
    }
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when(event){
                SignUpViewModel.SignUpNavigationEvent.NavigateToHome -> {
                    navController.navigate(NavRoutes.Home){
                        popUpTo(NavRoutes.Auth){
                            inclusive = true
                        }
                    }
                }
                SignUpViewModel.SignUpNavigationEvent.NavigateToLogin -> {
                    navController.navigate(NavRoutes.Login)
                }

                SignUpViewModel.SignUpNavigationEvent.ShowError -> {
                    showDialog = true
                }
            }
        }
    }
            Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.sign_up_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(20.dp))
            FoodHubTextField(
                value = name,
                onValueChange = { viewModel.onNameChange(it)},
                label = {
                    Text(
                        text = stringResource(R.string.full_name),
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth())

            FoodHubTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it)},
                label = {
                    Text(
                        text = stringResource(R.string.email),
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth())
            FoodHubTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it)},
                label = {
                    Text(
                        text = stringResource(R.string.password),
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Image(
                        painter = painterResource(R.drawable.passwordicon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                })
            Text(
                text = errorMessage.value ?: "",
                color = Color.Red
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = viewModel::onSignUpClick,
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Box(){
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                        }){
                        if (it){
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 32.dp).size(24.dp)
                            )
                        }
                        else{
                            Text(
                                text = stringResource(R.string.sign_up),
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(R.string.already_have_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        viewModel.onLoginClicked()
                    }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            GroupSocialButtons(
                onFacebookClick = {},
                onGoogleClick = {
                    viewModel.onGoogleSignInClicked(context = context)
                },
                color = Color.Black
            )
            if(showDialog){
                ModalBottomSheet(onDismissRequest = {
                    showDialog = false
                },
                    sheetState = sheetState) {
                    BasicDialog(
                        title = viewModel.error,
                        description = viewModel.errorDescription,
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                showDialog = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(rememberNavController())
}