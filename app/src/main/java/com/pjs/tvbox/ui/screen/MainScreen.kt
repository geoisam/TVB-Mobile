package com.pjs.tvbox.ui.screen

import com.pjs.tvbox.R

sealed class MainScreen(val route: String, val title: String, val chIconId: Int, val unIconId: Int) {
    data object Home : MainScreen("home", "首页", R.drawable.ic_home_fill, R.drawable.ic_home)
    data object Discover : MainScreen("discover", "发现", R.drawable.ic_explore_fill, R.drawable.ic_explore)
    data object Mine : MainScreen("mine", "我的", R.drawable.ic_person_fill, R.drawable.ic_person)
}