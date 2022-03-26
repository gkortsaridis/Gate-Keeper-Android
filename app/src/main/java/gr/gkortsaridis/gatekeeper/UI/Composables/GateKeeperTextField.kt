package gr.gkortsaridis.gatekeeper.UI.Composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme

enum class InputType(val type: Int) {
    TEXT(0),
    EMAIL(1),
    PASSWORD(2),
    NUMBER(3),
    PHONE(4),
    MULTILINE(5)
}

@Preview
@Composable
fun GateKeeperTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputType: InputType = InputType.TEXT,
    onTextChange: (String) -> Unit = {},
    value: String = ""
) {
    var textState by remember { mutableStateOf(value) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .composed { modifier },
        value = textState,
        label = { Text(text = placeholder) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = GateKeeperTheme.colorAccent,
        ),
        onValueChange = { textState = it; onTextChange(it) },
        singleLine = inputType != InputType.MULTILINE,
        maxLines = if(inputType == InputType.MULTILINE) 3 else 1,
        visualTransformation = if (inputType == InputType.PASSWORD && !passwordVisibility) PasswordVisualTransformation()  else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = when(inputType) {
                InputType.PASSWORD -> KeyboardType.Password
                InputType.EMAIL -> KeyboardType.Email
                InputType.NUMBER -> KeyboardType.Number
                InputType.PHONE -> KeyboardType.Phone
                else -> KeyboardType.Text },
        ),
        trailingIcon = {
            if(isFocused) {
                if(inputType == InputType.PASSWORD) {
                    val image = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(imageVector  = image, "")
                    }
                }else if (textState.isNotEmpty()) {
                    IconButton(onClick = { textState = ""; onTextChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}