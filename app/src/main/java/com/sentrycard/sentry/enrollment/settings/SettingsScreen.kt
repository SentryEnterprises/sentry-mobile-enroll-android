package com.sentrycard.sentry.enrollment.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sentrycard.sentry.enrollment.NfcViewModel
import com.sentrycard.sentry.enrollment.R
import com.sentrycard.sentry.enrollment.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (Screen) -> Unit,
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigate(Screen.GetCardState)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.options))
                },
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .padding(top = 150.dp)
                .fillMaxSize(),
        ) {
            Text(
                modifier = Modifier.padding(17.dp),
                text = stringResource(R.string.card_actions),
                color = Color.Gray
            )
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigate(Screen.Reset)
                    }
            ) {
                Text(
                    modifier = Modifier.padding(17.dp),
                    text = stringResource(R.string.reset_biometric_data),
                )
            }
            HorizontalDivider()

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                modifier = Modifier.padding(17.dp),
                text = stringResource(R.string.information),
                color = Color.Gray
            )
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigate(Screen.VersionInfo)
                    }
            ) {
                Text(
                    modifier = Modifier.padding(17.dp),
                    text = stringResource(R.string.retrieve_card_version_information),
                )
            }
        }
    }
}