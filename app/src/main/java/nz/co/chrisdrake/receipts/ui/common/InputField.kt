package nz.co.chrisdrake.receipts.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import nz.co.chrisdrake.receipts.ui.theme.AppTheme

data class InputFieldState(
    val label: String,
    val value: String = "",
    val error: String? = null,
    val onValueChanged: (String) -> Unit,
)

@Composable
fun InputField(
    field: InputFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val supportingText: @Composable (() -> Unit)? = field.error?.let {
        { Text(it) }
    }

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = field.value,
        onValueChange = field.onValueChanged,
        label = { Text(field.label) },
        singleLine = true,
        isError = field.error != null,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
    )
}

@Composable
fun EmailInputField(field: InputFieldState) {
    InputField(
        field = field,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.semantics { contentType = ContentType.EmailAddress },
    )
}

@Composable
fun PasswordInputField(field: InputFieldState) {
    InputField(
        field = field,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.semantics { contentType = ContentType.Password },
    )
}

@Preview
@Composable
private fun Preview_InputField_Empty() {
    AppTheme {
        InputField(
            field = InputFieldState(
                label = "Input Field",
                value = "",
                error = null,
                onValueChanged = {},
            ),
        )
    }
}

@Suppress("FunctionName")
fun preview_InputFieldState(label: String, value: String = "") = InputFieldState(
    label = label,
    value = value,
    onValueChanged = {},
)

@Preview
@Composable
private fun Preview_InputField_WithValue() {
    AppTheme {
        InputField(
            field = InputFieldState(
                label = "Input Field",
                value = "Sample Value",
                error = null,
                onValueChanged = {},
            ),
        )
    }
}

@Preview
@Composable
private fun Preview_InputField_WithError() {
    AppTheme {
        InputField(
            field = InputFieldState(
                label = "Input Field",
                value = "",
                error = "Required",
                onValueChanged = {},
            ),
        )
    }
}
