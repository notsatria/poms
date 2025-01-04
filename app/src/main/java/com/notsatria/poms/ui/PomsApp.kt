package com.notsatria.poms.ui

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.notsatria.poms.ui.theme.PomsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomsApp(modifier: Modifier = Modifier) {
    Scaffold(modifier,
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Pomodoro Timer") }) }
    ) { innerPadding ->

    }
}

@Preview
@Composable
fun PomsAppPreview() {
    PomsTheme {
        PomsApp()
    }
}