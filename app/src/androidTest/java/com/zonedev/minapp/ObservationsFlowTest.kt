// Ubicación: app/src/androidTest/java/com/zonedev/minapp/ObservationsFlowTest.kt
package com.zonedev.minapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zonedev.minapp.ui.Screen.Guardia.Observations
import com.zonedev.minapp.util.ObservationsTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ObservationsFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * PRUEBA DEL "CAMINO FELIZ" (HAPPY PATH)
     *
     * Objetivo: Verificar que un usuario puede llenar los campos y enviar un reporte
     * de observación exitosamente, viendo el modal de confirmación y el reseteo de los campos.
     *
     * MEJORAS APLICADAS:
     * - Se busca el modal por su testTag para evitar fragilidad.
     * - Se verifica que el indicador de carga desaparece.
     * - Se reduce el timeout, asumiendo una conexión de prueba decente.
     */
    @Test
    fun enviarReporteExitoso_muestraModalDeConfirmacion() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Observations(guardiaId = "testGuardia123")
        }

        val asuntoDePrueba = "Luz parpadeando en pasillo 3"
        val observacionDePrueba = "La luz del tercer piso, sección norte, está parpadeando."
        val textoBotonEnviar = composeTestRule.activity.getString(R.string.button_submit)

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBJECT_FIELD).performTextInput(asuntoDePrueba)
        composeTestRule.onNodeWithTag(ObservationsTestTags.OBSERVATION_FIELD).performTextInput(observacionDePrueba)
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        // 1. Esperar a que aparezca el modal de éxito, ahora buscado por su tag.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag(ObservationsTestTags.SUCCESS_MODAL).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUCCESS_MODAL).assertIsDisplayed()

        // 2. Cerrar el modal para poder verificar el estado de la UI.
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUCCESS_MODAL).performClick()

        // 3. Verificar que los campos de texto se han limpiado.
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBJECT_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(ObservationsTestTags.OBSERVATION_FIELD).assertTextEquals("")

        // 4. (MEJORA) Verificar que el estado de carga ha terminado (el texto del botón volvió a la normalidad).
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBMIT_BUTTON).assertTextEquals(textoBotonEnviar)
    }

    /**
     * PRUEBA DEL "CAMINO TRISTE" (SAD PATH) - Validación local
     *
     * Objetivo: Verificar que si el usuario intenta enviar el formulario con campos vacíos,
     * se muestra un modal de error.
     */
    @Test
    fun enviarReporteFallido_camposVacios_muestraModalDeError() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Observations(guardiaId = "testGuardia123")
        }

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        // (MEJORA) Afirmar que el modal de error está visible usando su testTag.
        composeTestRule.onNodeWithTag(ObservationsTestTags.ERROR_MODAL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ObservationsTestTags.ERROR_MODAL).performClick() // Cerrar modal
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBMIT_BUTTON).assertIsDisplayed()
    }

    /**
     * PRUEBA DE CASOS LÍMITE (EDGE CASES)
     *
     * Objetivo: Verificar que la UI no se rompe y el envío funciona con
     * entradas de texto muy largas y con caracteres especiales.
     */
    @Test
    fun enviarReporte_conTextoLargoYEspecial_noCrasheaYEnvia() {
        // --- PREPARACIÓN ---
        composeTestRule.setContent {
            Observations(guardiaId = "testGuardia123")
        }
        val textoLargo = "a".repeat(500) + "\n" + "b".repeat(500)
        val textoConSimbolos = "Título con emojis 🚨 y símbolos extraños: !@#$%^&*()_+-=[]{}|;':,./<>?`~"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBJECT_FIELD).performTextInput(textoConSimbolos)
        composeTestRule.onNodeWithTag(ObservationsTestTags.OBSERVATION_FIELD).performTextInput(textoLargo)
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUBMIT_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        // Solo verificamos que el modal de éxito aparece. Esto confirma que la app
        // procesó la entrada sin crashear y la lógica de envío se completó.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag(ObservationsTestTags.SUCCESS_MODAL).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag(ObservationsTestTags.SUCCESS_MODAL).assertIsDisplayed()
    }
}