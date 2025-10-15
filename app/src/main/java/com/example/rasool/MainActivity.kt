package com.example.rasool

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rasool.data.FoodApi
import com.example.rasool.ui.features.auth.AuthScreen
import com.example.rasool.ui.features.auth.signup.SignUpScreen
import com.example.rasool.ui.navigation.NavRoutes
import com.example.rasool.ui.theme.RasoolTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var foodApi: FoodApi
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RasoolTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Auth,
                        modifier = Modifier.padding(innerPadding)){

                        composable(NavRoutes.SignUp){
                            SignUpScreen(navController = navController)
                        }
                        composable(NavRoutes.Auth){
                            AuthScreen(navController = navController)
                        }
                        composable(NavRoutes.Login){
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                            )
                        }
                        composable(NavRoutes.Home){
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Green)
                            )
                        }
                    }
                }
            }
        }
        if(::foodApi.isInitialized){
            Log.d("Retrofit", "Initialized")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RasoolTheme {
        Greeting("Android")
    }
}