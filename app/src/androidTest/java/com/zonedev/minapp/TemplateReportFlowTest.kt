// Ubicación: app/src/androidTest/java/com/zonedev/minapp/TemplateReportFlowTest.kt
package com.zonedev.minapp

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zonedev.minapp.ui.theme.Templates.Template_Text
import com.zonedev.minapp.util.TemplateTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TemplateReportFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * PRUEBA DEL "CAMINO FELIZ" PARA REPORTE TIPO "PERSONAL"
     *
     * Objetivo: Verificar que se puede llenar y enviar un reporte de tipo Personal
     * exitosamente, mostrando el modal de confirmación y limpiando los campos.
     */
    @Test
    fun enviarReportePersonal_Exitoso_muestraModalYLimpiaCampos() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Template_Text(Tipo_Report = "Personal", guardiaId = "testGuardia456")
        }

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).performTextInput("123456789")
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).performTextInput("Juan Perez")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESTINY_FIELD).performTextInput("Oficina 301")
        composeTestRule.onNodeWithTag(TemplateTestTags.AUTHORIZATION_FIELD).performTextInput("Gerencia")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESCRIPTION_FIELD).performTextInput("Visita programada")
        composeTestRule.onNodeWithTag(TemplateTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        // Se usa assertExists() que es menos "flaky" que buscar en una lista de nodos.
        composeTestRule.onNodeWithTag(TemplateTestTags.SUCCESS_MODAL).assertExists()
        composeTestRule.onNodeWithTag(TemplateTestTags.SUCCESS_MODAL).performClick() // Cerrar modal

        // Verificar que los campos se han limpiado tras el éxito.
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESTINY_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.AUTHORIZATION_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESCRIPTION_FIELD).assertTextEquals("")
    }

    /**
     * PRUEBA DEL "CAMINO FELIZ" PARA REPORTE TIPO "VEHICULAR"
     *
     * Objetivo: Verificar que un reporte vehicular con placa válida se envía correctamente
     * y que los campos se limpian.
     */
    @Test
    fun enviarReporteVehicular_conFormatoPlacaValido_muestraModalYLimpiaCampos() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Template_Text(Tipo_Report = "Vehicular", guardiaId = "testGuardia456")
        }

        // --- ACCIÓN ---
        // Se usa el formato correcto "ABC-123" que la regex espera.
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).performTextInput("ABC-123")
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).performTextInput("Maria Rodriguez")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESTINY_FIELD).performTextInput("Parqueadero Visitantes")
        composeTestRule.onNodeWithTag(TemplateTestTags.AUTHORIZATION_FIELD).performTextInput("Recepcion")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESCRIPTION_FIELD).performTextInput("Entrega de paquete")
        composeTestRule.onNodeWithTag(TemplateTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.SUCCESS_MODAL).assertExists()
        composeTestRule.onNodeWithTag(TemplateTestTags.SUCCESS_MODAL).performClick()

        // Se añade la verificación de limpieza de campos que faltaba.
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESTINY_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.AUTHORIZATION_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESCRIPTION_FIELD).assertTextEquals("")
    }

    /**
     * PRUEBA DE VALIDACIÓN PARA REPORTE TIPO "VEHICULAR"
     *
     * Objetivo: Verificar que la validación de formato de placa es estricta y que los
     * datos del usuario se conservan tras el error para su corrección.
     */
    @Test
    fun enviarReporteVehicular_conFormatoPlacaInvalido_muestraErrorYConservaDatos() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Template_Text(Tipo_Report = "Vehicular", guardiaId = "testGuardia456")
        }
        val placaInvalida = "INVALIDO"
        val nombreConductor = "Conductor de Prueba"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).performTextInput(placaInvalida)
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).performTextInput(nombreConductor)
        composeTestRule.onNodeWithTag(TemplateTestTags.DESTINY_FIELD).performTextInput("Destino")
        composeTestRule.onNodeWithTag(TemplateTestTags.AUTHORIZATION_FIELD).performTextInput("Autoriza")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESCRIPTION_FIELD).performTextInput("Descripcion")
        composeTestRule.onNodeWithTag(TemplateTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.VALIDATION_ERROR_MODAL).assertExists()
        composeTestRule.onNodeWithTag(TemplateTestTags.VALIDATION_ERROR_MODAL).performClick() // Cerrar modal

        // Verificar que los datos del usuario no se borraron tras el error.
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).assertTextEquals(placaInvalida)
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).assertTextEquals(nombreConductor)
    }

    /**
     * PRUEBA DE VALIDACIÓN DE CAMPOS VACÍOS
     *
     * Objetivo: Verificar que si se intenta enviar el formulario con campos obligatorios
     * vacíos, se muestra el modal de error y se conservan los datos ya ingresados.
     */
    @Test
    fun enviarReporte_conCamposVacios_muestraErrorYConservaDatos() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Template_Text(Tipo_Report = "Personal", guardiaId = "testGuardia456")
        }
        val idIngresado = "987654321"
        val nombreIngresado = "Carlos Gomez"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).performTextInput(idIngresado)
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).performTextInput(nombreIngresado)
        composeTestRule.onNodeWithTag(TemplateTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.VALIDATION_ERROR_MODAL).assertExists()
        composeTestRule.onNodeWithTag(TemplateTestTags.VALIDATION_ERROR_MODAL).performClick()

        // Verificar que los datos que sí se ingresaron no se borraron.
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).assertTextEquals(idIngresado)
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).assertTextEquals(nombreIngresado)
    }

    /**
     * PRUEBA DE CASOS LÍMITE (EDGE CASES)
     *
     * Objetivo: Verificar que el envío funciona con entradas de texto muy largas y con
     * caracteres especiales, sin que la UI o la lógica fallen.
     */
    @Test
    fun enviarReporte_conTextoLargoYEspecial_noCrasheaYEnvia() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Template_Text(Tipo_Report = "Personal", guardiaId = "testGuardia456")
        }
        val textoLargo = "Este es un texto extremadamente largo ".repeat(30) + "\n con saltos de línea."
        val textoConSimbolos = "Nómbre cön caractéres extrañoś y emojis 🚀: !@#$%^&*()"

        // --- ACCIÓN ---
        // Se prueba un ID numérico muy largo que podría causar problemas de conversión.
        composeTestRule.onNodeWithTag(TemplateTestTags.ID_FIELD).performTextInput("112233445566778899")
        composeTestRule.onNodeWithTag(TemplateTestTags.NAME_FIELD).performTextInput(textoConSimbolos)
        composeTestRule.onNodeWithTag(TemplateTestTags.DESTINY_FIELD).performTextInput(textoLargo)
        composeTestRule.onNodeWithTag(TemplateTestTags.AUTHORIZATION_FIELD).performTextInput("Autorización con \n saltos")
        composeTestRule.onNodeWithTag(TemplateTestTags.DESCRIPTION_FIELD).performTextInput(textoLargo)
        composeTestRule.onNodeWithTag(TemplateTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(TemplateTestTags.SUCCESS_MODAL).assertExists()
    }
}
