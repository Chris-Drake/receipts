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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import nz.co.chrisdrake.receipts.R
import nz.co.chrisdrake.receipts.ui.theme.AppTheme
import java.time.LocalTime

data class TimeFieldState(
    val selection: LocalTime? = null,
    val onTimeSelected: (LocalTime) -> Unit,
) {
    val formattedValue: String = selection?.let(TIME_FORMATTER::format) ?: ""
}

@Composable
fun TimeField(field: TimeFieldState, modifier: Modifier = Modifier) {
    var showTimePicker by remember(field) { mutableStateOf(false) }

    ClickableTextField(
        modifier = modifier,
        value = field.formattedValue,
        label = { Text(stringResource(R.string.common_time_dialog_title)) },
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
                Text(stringResource(R.string.common_dialog_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSet(LocalTime.of(state.hour, state.minute)) }
            ) {
                Text(stringResource(R.string.common_dialog_confirm))
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
