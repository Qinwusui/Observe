package xyz.with.observe.ui

sealed class Screen(val route: String) {
    object MainView : Screen("MainView")
    object NewsView : Screen("NewsView")
}