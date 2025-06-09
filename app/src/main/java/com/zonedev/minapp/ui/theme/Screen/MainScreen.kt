package com.zonedev.minapp.ui.theme.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.primary

@Composable
fun MainScreen(navController: NavController) {

    val logo = painterResource(R.drawable.logo_minapp)
    val banner = painterResource(R.drawable.arrow__2196f3)

    Box(modifier = Modifier
        .background(background)
        .statusBarsPadding()
        .clickable{navController.navigate("login") }
    ) {
        Image(
            painter = banner,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 295.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = logo,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(200.dp)
                    .padding(top = 40.dp, start = 25.dp)
            )
            ContainerText()
        }
    }
}

@Composable
fun ContainerText() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(text = stringResource(R.string.app_name), color = primary, fontSize = 60.sp, fontWeight = FontWeight.Bold)
        Text(text = stringResource(R.string.lema_app), color = primary, fontSize = 30.sp, fontWeight = FontWeight.W300, textAlign = TextAlign.Center)
    }
}