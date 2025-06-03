package nz.co.chrisdrake.receipts.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nz.co.chrisdrake.receipts.ui.theme.AppTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

data class TimeFieldState(
    val selection: LocalTime? = null,
    val onTimeSelected: (LocalTime) -> Unit,
) {
    val formattedValue: String = selection?.let(timeFormatter::format) ?: ""
}

@Composable
fun TimeField(field: TimeFieldState, modifier: Modifier = Modifier) {
    var showTimePicker by remember(field) { mutableStateOf(false) }

    ClickableTextField(
        modifier = modifier,
        value = field.formattedValue,
        label = { Text("Time") },
        onClick = { showTimePicker = true },
    )

    if (showTimePicker) {
        TimePicker(
            initialSelection = field.selection ?: LocalTime.now(),
            onDismiss = { showTimePicker = false },
            onSet = field.onTimeSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePicker(
    initialSelection: LocalTime,
    onDismiss: () -> Unit,
    onSet: (LocalTime) -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialSelection.hour,
        initialMinute = initialSelection.minute,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {},
        text = { androidx.compose.material3.TimePicker(state = state) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSet(LocalTime.of(state.hour, state.minute)) }
            ) {
                Text("Confirm")
            }
        },
    )
}

@Preview
@Composable
private fun Preview_TimeField() {
    AppTheme {
        TimeField(
            field = TimeFieldState(
                selection = LocalTime.of(12, 0),
                onTimeSelected = {},
            ),
        )
    }
}

@Preview
@Composable
private fun Preview_TimePicker() {
    AppTheme {
        TimePicker(
            initialSelection = LocalTime.of(12, 0),
            onDismiss = {},
            onSet = {},
        )
    }
}