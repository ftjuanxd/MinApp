package com.zonedev.minapp.ui.theme.Screen.Guardia

import androidx.compose.runtime.Composable
import com.zonedev.minapp.ui.theme.Templates.Template_Text

@Composable
fun Personal(guardiaId: String) {
    Template_Text(Tipo_Report = "Personal",guardiaId = guardiaId)
}