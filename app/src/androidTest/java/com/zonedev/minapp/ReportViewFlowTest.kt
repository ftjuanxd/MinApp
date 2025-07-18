// Ubicación: app/src/androidTest/java/com/zonedev/minapp/ReportViewFlowTest.kt
package com.zonedev.minapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zonedev.minapp.ui.Screen.ScreenReport
import com.zonedev.minapp.util.ReportViewTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportViewFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val guardiaIdDePrueba = "testGuardiaParaFiltros"

    @Before
    fun setup() {
        // Lanzamos la pantalla de reportes antes de cada prueba.
        composeTestRule.setContent {
            ScreenReport(guardiaId = guardiaIdDePrueba)
        }
    }

    /**
     * PRUEBA DEL "CAMINO FELIZ"
     *
     * Objetivo: Verificar que al aplicar un filtro de ID válido, la lista muestra
     * el reporte correcto y se puede abrir su detalle.
     *
     * REQUISITO: Debe existir un reporte de tipo "Personal" para el guardiaIdDePrueba
     * con el `Id_placa` = "12345" y `Nombre` = "Juan De Prueba".
     * Y otro reporte con `Id_placa` = "67890" para verificar el filtrado.
     */
    @Test
    fun filtroPorId_muestraReporteCorrecto_yConservaFiltroTrasCerrarModal() {
        val idDePrueba = "12345"
        val nombreDePrueba = "Juan De Prueba"
        val otroReporteId = "67890"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.ID_FILTER_FIELD).performTextInput(idDePrueba)

        // Se usa waitUntil para esperar de forma explícita a que la UI se actualice
        // antes de realizar la aserción, haciéndola más robusta.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(nombreDePrueba).fetchSemanticsNodes().isNotEmpty()
        }

        // Se hace clic directamente en la fila usando su testTag, que es más fiable.
        composeTestRule.onNodeWithTag(ReportViewTestTags.reportRow(nombreDePrueba)).performClick()

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.DETAILS_MODAL).assertIsDisplayed()
        composeTestRule.onNodeWithText("Id_placa: $idDePrueba").assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReportViewTestTags.DETAILS_MODAL_CLOSE_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ReportViewTestTags.DETAILS_MODAL).assertDoesNotExist()

        // Verificar que el filtro se mantiene después de cerrar el modal.
        composeTestRule.onNodeWithTag(ReportViewTestTags.ID_FILTER_FIELD).assertTextEquals(idDePrueba)
        // Verificar que la lista sigue filtrada (el otro reporte no debe estar).
        composeTestRule.onNodeWithText(otroReporteId).assertDoesNotExist()
    }

    /**
     * PRUEBA DE FILTRO COMBINADO (ID + NOMBRE)
     *
     * Objetivo: Asegurar que la búsqueda funciona correctamente cuando se aplican
     * múltiples criterios de filtro simultáneamente.
     */
    @Test
    fun filtroCombinado_porIdYNombre_muestraResultadoCorrecto() {
        val idDePrueba = "12345"
        val nombreDePrueba = "Juan De Prueba"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.ID_FILTER_FIELD).performTextInput(idDePrueba)
        composeTestRule.onNodeWithTag(ReportViewTestTags.NAME_FILTER_FIELD).performTextInput(nombreDePrueba)

        // --- VERIFICACIÓN ---
        // Se elimina el timestamp. La aserción espera a que el nodo con el texto
        // del nombre exista, lo cual es determinista.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(nombreDePrueba).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(nombreDePrueba).assertExists()
    }

    /**
     * PRUEBA DE BÚSQUEDA SOLO POR NOMBRE
     *
     * Objetivo: Verificar que el filtro por nombre funciona de forma independiente.
     */
    @Test
    fun filtroPorNombre_muestraResultadoCorrecto() {
        val nombreDePrueba = "Juan De Prueba"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.NAME_FILTER_FIELD).performTextInput(nombreDePrueba)

        // --- VERIFICACIÓN ---
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(nombreDePrueba).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(nombreDePrueba).assertExists()
    }

    /**
     * PRUEBA DE CAMBIO DE FILTROS
     *
     * Objetivo: Verificar que al cambiar el tipo de reporte en el dropdown,
     * los campos de filtro en la UI se actualizan correctamente.
     */
    @Test
    fun alCambiarTipoReporte_aVehicular_seMuestranFiltrosCorrectos() {
        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.DROPDOWN_BUTTON).performClick()
        composeTestRule.onNodeWithTag(ReportViewTestTags.dropdownItem("Vehicular")).performClick()

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.DROPDOWN_BUTTON).assertTextEquals("Vehicular")
        composeTestRule.onNodeWithTag(ReportViewTestTags.ID_FILTER_FIELD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ReportViewTestTags.NAME_FILTER_FIELD).assertIsDisplayed()
    }

    /**
     * PRUEBA DE ESTADO VACÍO
     *
     * Objetivo: Verificar que si un filtro no produce resultados, se muestra
     * el mensaje de "No se han encontrado reportes".
     */
    @Test
    fun filtroSinResultados_muestraMensajeDeListaVacia() {
        val idInexistente = "id_que_no_existe_en_la_db_999"

        // --- ACCIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.ID_FILTER_FIELD).performTextInput(idInexistente)

        // --- VERIFICACIÓN ---
        composeTestRule.onNodeWithTag(ReportViewTestTags.EMPTY_LIST_MESSAGE).assertExists()
    }

    /**
     * PRUEBA DE ESTADO INICIAL
     *
     * Objetivo: Verificar el estado de la pantalla justo después de cargar.
     */
    @Test
    fun estadoInicial_muestraControlesPorDefecto() {
        // --- VERIFICACIÓN ---
        // 1. La lista de reportes debe ser visible.
        composeTestRule.onNodeWithTag(ReportViewTestTags.REPORT_LIST_CONTAINER).assertIsDisplayed()

        // 2. El mensaje de "lista vacía" NO debe existir (asumiendo que hay datos).
        composeTestRule.onNodeWithTag(ReportViewTestTags.EMPTY_LIST_MESSAGE).assertDoesNotExist()

        // 3. Los campos de filtro deben estar vacíos.
        composeTestRule.onNodeWithTag(ReportViewTestTags.ID_FILTER_FIELD).assertTextEquals("")
        composeTestRule.onNodeWithTag(ReportViewTestTags.NAME_FILTER_FIELD).assertTextEquals("")

        // 4. MEJORA: El dropdown debe mostrar el valor por defecto.
        composeTestRule.onNodeWithTag(ReportViewTestTags.DROPDOWN_BUTTON).assertTextEquals("Personal")
    }

    /*
    NOTA SOBRE PRUEBAS DE DATOS CORRUPTOS:

    -   Datos Corruptos: Probar cómo reacciona el modal de detalles a un reporte con campos
        nulos o faltantes también es ideal para una prueba unitaria o una prueba contra un
        repositorio "Fake" donde se puede construir un objeto de reporte inválido a propósito.
    */
}
