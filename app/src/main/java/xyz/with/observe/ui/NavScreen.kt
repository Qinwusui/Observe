package xyz.with.observe.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import xyz.with.observe.theme.statusBarColor
import xyz.with.observe.viewmodel.MainViewModel

@Composable
fun NavScreen(mainViewModel: MainViewModel) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setStatusBarColor(statusBarColor, false)
        systemUiController.setSystemBarsColor(statusBarColor, false)
    })
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            Screen.MainView.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.MainView.route) {
                MainView(mainViewModel = mainViewModel, navController)
            }
            composable(Screen.NewsView.route) {
                NewsView(mainViewModel = mainViewModel)
            }
        }
    }
}