package com.pjs.tvbox.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.pjs.tvbox.R
import com.pjs.tvbox.data.UpdateData
import com.pjs.tvbox.ui.theme.LogoFont
import com.pjs.tvbox.util.AppUtil
import com.pjs.tvbox.util.UpdateUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SplashScreen {
                    lifecycleScope.launch {
                        val update = UpdateData.getUpdate(this@SplashActivity)
                        if (update != null) {
                            val remoteVersionName =
                                update.versionName?.replace(".", "")?.toLongOrNull() ?: 0L
                            val localVersionName =
                                AppUtil.getVersionName(this@SplashActivity).replace(".", "")
                                    .toLongOrNull() ?: 0L
                            if (remoteVersionName > localVersionName) {
                                UpdateUtil.setUpdate(update)
                            } else {
                                UpdateUtil.clearUpdate()
                            }
                        }
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(666)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo_fill),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = LogoFont,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
            )
        }
    }
}