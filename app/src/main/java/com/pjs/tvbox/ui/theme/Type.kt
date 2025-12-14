package com.pjs.tvbox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pjs.tvbox.R

val LogoFont =
    FontFamily(
        Font(
            R.font.science_gothic,
            FontWeight.Normal
        )
    )

val typography =
    Typography(
        displayLarge = TextStyle(
            fontSize = 66.sp,
            letterSpacing = 0.sp,
        ),
        displayMedium = TextStyle(
            fontSize = 55.sp,
            letterSpacing = 0.sp,
        ),
        displaySmall = TextStyle(
            fontSize = 44.sp,
            letterSpacing = 0.sp,
        ),
        headlineLarge = TextStyle(
            fontSize = 36.sp,
            letterSpacing = 0.sp,
        ),
        headlineMedium = TextStyle(
            fontSize = 32.sp,
            letterSpacing = 0.sp,
        ),
        headlineSmall = TextStyle(
            fontSize = 28.sp,
            letterSpacing = 0.sp,
        ),
        titleLarge = TextStyle(
            fontSize = 24.sp,
            letterSpacing = 0.sp,
        ),
        titleMedium = TextStyle(
            fontSize = 22.sp,
            letterSpacing = 0.sp,
        ),
        titleSmall = TextStyle(
            fontSize = 20.sp,
            letterSpacing = 0.sp,
        ),
        bodyLarge = TextStyle(
            fontSize = 18.sp,
            letterSpacing = 0.sp,
        ),
        bodyMedium = TextStyle(
            fontSize = 16.sp,
            letterSpacing = 0.sp,
        ),
        bodySmall = TextStyle(
            fontSize = 15.sp,
            letterSpacing = 0.sp,
        ),
        labelLarge = TextStyle(
            fontSize = 14.sp,
            letterSpacing = 0.sp,
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            letterSpacing = 0.sp,
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            letterSpacing = 0.sp,
        ),
    )