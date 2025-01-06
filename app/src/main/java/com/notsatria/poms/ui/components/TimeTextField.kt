package com.notsatria.poms.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TimeTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
) {
    OutlinedTextField(
        modifier = modifier,
        label = {
            Text(text = labelText)
        },
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Timer, contentDescription = null)
        },
        suffix = {
            Text(text = "Minutes")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}