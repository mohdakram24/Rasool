package com.example.rasool.ui.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rasool.R
import com.example.rasool.ui.BasicDialog
import com.example.rasool.ui.GroupSocialButtons
import com.example.rasool.ui.navigation.NavRoutes
import com.example.rasool.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    val viewModel: AuthScreenViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val imageSize = remember {
        mutableStateOf(IntSize.Zero)
    }
    val context = LocalContext.current
    val brush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black
        ),
        startY = imageSize.value.height.toFloat() / 3
    )
    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when(event){
                AuthScreenViewModel.AuthNavigationEvent.NavigateToHome -> {
                    navController.navigate(NavRoutes.Home){
                        popUpTo(NavRoutes.Auth){
                            inclusive = true
                        }
                    }
                }
                AuthScreenViewModel.AuthNavigationEvent.NavigateToSignUp ->{
                    navController.navigate(NavRoutes.SignUp)
                }
                AuthScreenViewModel.AuthNavigationEvent.ShowErrorDialog -> {
                    showDialog = true
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)){
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.onGloballyPositioned{
                imageSize.value = it.size
            },
            alpha = 0.8f)
        Box(
            modifier = Modifier.matchParentSize().background(brush = brush)
        )
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.skip),
                color = Orange
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 110.dp)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome),
                color = Color.Black,
                modifier = Modifier,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.app_name),
                color = Orange,
                modifier = Modifier,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.food_hub_desc),
                fontSize = 20.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupSocialButtons(
                onGoogleClick = {
                    viewModel.onGoogleSignInClicked(context = context)
                },
                onFacebookClick = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate(NavRoutes.SignUp)
            },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = 32.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f))) {
                Text(
                    text = stringResource(R.string.create_account),
                    color = Color.White
                )
            }
            TextButton(onClick = {
                navController.navigate(NavRoutes.Login)
            }) {
                Text(
                    text = stringResource(R.string.already_have_account),
                    color = Color.White
                )
            }
        }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAuth(){
    AuthScreen(rememberNavController())
}