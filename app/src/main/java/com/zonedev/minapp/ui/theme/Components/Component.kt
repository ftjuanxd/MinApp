package com.zonedev.minapp.ui.theme.Components


import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import com.zonedev.minapp.ui.theme.text

class PlacaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // El texto de entrada es el dato "crudo", ej: "ABC123"
        val inputText = text.text

        val formattedText = buildString {
            // Añade las primeras 3 letras
            append(inputText.take(3))

            // Si hay números, añade el guion
            if (inputText.length > 3) {
                append("-")
                // Añade los números (los caracteres que siguen a los 3 primeros)
                append(inputText.substring(3))
            }
        }

        // El OffsetMapping es la clave para que el cursor no se mueva incorrectamente
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Mueve el cursor 1 posición a la derecha si está después de las letras
                return if (offset > 3) offset + 1 else offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Mueve el cursor 1 posición a la izquierda si está después del guion
                return if (offset > 4) offset - 1 else offset
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

@Composable
fun ButtonApp(
    text: String,
    iconButton: Boolean? = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(color_component),
        shape = RoundedCornerShape(12.dp),
        enabled = isEnabled
    ) {
        Text(text = text, color = Color.White, fontSize = 18.sp)
        if (iconButton == true) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.White,
            )
        }
    }
}

@Composable
fun CheckHold(): MutableState<Boolean> {
    // Estado del Checkbox
    val isChecked = remember { mutableStateOf(false) }

    // Contenedor con el Checkbox y un Text para mostrar el estado
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 190.dp)
    ) {
        Box(
            modifier = Modifier
                .size(23.dp)  // Tamaño del checkbox
                .border(2.dp, primary, RoundedCornerShape(4.dp))  // Borde personalizado
                .padding(1.dp)  // Espacio entre el borde y el checkbox
        ) {
            Checkbox(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it }, // Actualiza el estado cuando se hace clic
                colors = CheckboxDefaults.colors(
                    checkedColor = primary,        // Color cuando está marcado
                    uncheckedColor = background,   // Color cuando está desmarcado
                    checkmarkColor = background    // Color del check
                )
            )
        }
        Text(
            text = stringResource(R.string.Name_CheckHolder),
            fontSize = 15.sp,
            modifier = Modifier.padding(all = 8.dp)
        )
    }
    return isChecked
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
    @DrawableRes trailingIcon: Int? = null,
    @ColorRes iconTint: Int? = null,
    pdHeight: Dp? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isUser: Boolean? = null,
    isPasswordField: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Determina la alineación según isUser
    val alignmentModifier = when (isUser) {
        true -> Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
        false -> Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
        else -> Modifier.fillMaxWidth()
    }

    // Determina el color final que se usará para el tinte del icono
    val resolvedIconTint = when {
        iconTint != null -> colorResource(id = iconTint) // Si se proporciona un ID de recurso, úsalo
        else -> Color.Black // Por defecto si no se proporciona ninguno
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = color_component) },
        enabled = isEnabled,
        readOnly = onClick != null,
        modifier = alignmentModifier
            .padding(vertical = 8.dp)
            .border(2.dp, primary, RoundedCornerShape(12.dp))
            .let { if (pdHeight != null) it.height(pdHeight) else it }
            .clickable {
                onClick?.invoke()
            },
        visualTransformation = if (isPasswordField && !passwordVisible) PasswordVisualTransformation() else visualTransformation,
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            if (isPasswordField)
            {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                Icon(
                    imageVector = image,
                    contentDescription = description,
                    tint = resolvedIconTint,
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            } else if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    tint = resolvedIconTint
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledLabelColor = Color.Transparent,
            containerColor = background,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = text,
            focusedTextColor = text
        )
    )
}

// Create Modal
@Composable
fun Modal(showDialog: Boolean, onDismiss: () -> Unit,  @StringRes title: Int, @StringRes Message: Int, @StringRes textButton: Int = R.string.Value_Button_Report, onClick: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(
                text = stringResource(title),
                color= color_component,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),)
            },
            text = { Text(
                text = stringResource(Message),
                color = text,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 6.dp))
            },
            confirmButton = {
                ButtonApp(
                    text = stringResource(textButton),
                    onClick = onClick,
                )
            }
        )
    }
}

@Composable
fun Separator() {
    HorizontalDivider(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(top = 10.dp),
        thickness = 1.dp,
        color = Color.Gray
    )
}

@Composable
fun Space(height: Dp = 8.dp){
    Spacer(modifier= Modifier.height(height))
}

