package nz.co.chrisdrake.receipts.ui.common

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nz.co.chrisdrake.receipts.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset

data class DateFieldState(
    val selection: LocalDate? = null,
    val error: String? = null,
    val onDateSelected: (LocalDate) -> Unit,
) {
    val formattedValue: String = selection?.let(DATE_FORMATTER::format) ?: ""
}

@Composable
fun DateField(field: DateFieldState, modifier: Modifier = Modifier) {
    var showDatePicker by remember(field) { mutableStateOf(false) }

    ClickableTextField(
        modifier = modifier,
        value = field.formattedValue,
        label = { Text("Date") },
        error = field.error,
        onClick = { showDatePicker = true },
    )

    if (showDatePicker) {
        DatePicker(
            initialSelection = field.selection ?: LocalDate.now(),
            onDismiss = { showDatePicker = false },
            onSet = field.onDateSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePicker(
    initialSelection: LocalDate,
    onDismiss: () -> Unit,
    onSet: (LocalDate) -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialSelection.atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli(),
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDate = state.selectedDateMillis
                        ?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
                        ?: LocalDate.now()
                    onSet(selectedDate)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        androidx.compose.material3.DatePicker(state = state)
    }
}

@Preview
@Composable
private fun Preview_DateField() {
    AppTheme {
        DateField(
            field = DateFieldState(
                selection = LocalDate.of(1970, Month.JANUARY, 1),
                onDateSelected = {},
            ),
        )
    }
}

@Preview
@Composable
private fun Preview_DatePicker() {
    AppTheme {
        DatePicker(
            initialSelection = LocalDate.of(1970, Month.JANUARY, 1),
            onDismiss = {},
            onSet = {},
        )
    }
}