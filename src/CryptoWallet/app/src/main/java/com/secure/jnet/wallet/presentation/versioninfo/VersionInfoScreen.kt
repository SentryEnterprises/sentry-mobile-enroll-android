package com.secure.jnet.wallet.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api

import com.secure.jnet.wallet.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.presentation.NAV_GET_CARD_STATE
import com.secure.jnet.wallet.presentation.NAV_SETTINGS
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.util.fontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionInfoScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (String) -> Unit,
) {

    LaunchedEffect(Unit) {
        nfcViewModel.startNfcAction(NfcAction.GetVersionInformation)
    }

    Scaffold(
        contentColor = Color.Black,
        containerColor = Color.Black,
        modifier = Modifier.background(Color.Black),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigate(NAV_SETTINGS)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text("Version Information", fontFamily = fontFamily)
                },
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .fillMaxSize(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))
                LottieAnimation(composition)

                Text(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                    color = Color.White,
                    text = "Place your card on a flat, non-metallic surface then place the phone on top.",
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily,
                    fontSize = 17.sp
                )
            }

            val info = listOf(
                "SDK Version",
                "OS Version",
                "Enroll Version",
                "CVM Version",
                "Verify Version"

            )

            LazyColumn {
                info.forEach {
                    item {
                        Text(
                            modifier = Modifier.padding(17.dp),
                            text = it,
                            fontFamily = fontFamily,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}