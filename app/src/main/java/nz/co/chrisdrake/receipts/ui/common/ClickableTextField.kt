package nz.co.chrisdrake.receipts.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ClickableTextField(
    value: String,
    label: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            onClick()
        }
    }

    val supportingText: @Composable (() -> Unit)? = error?.let {
        { Text(it) }
    }

    TextField(
        modifier = modifier,
        interactionSource = interactionSource,
        value = value,
        onValueChange = {},
        label = label,
        singleLine = true,
        isError = error != null,
        supportingText = supportingText,
        readOnly = true,
    )
}