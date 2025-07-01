package com.zonedev.minapp.ui.theme.Screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.Template_Text

@Composable
fun Vehicular(guardiaId: String) {
    Template_Text(Label_Id = stringResource(R.string.Value_Label_Vehicular), Tipo_Report = "Vehicular",guardiaId = guardiaId)
}
