package com.zonedev.minapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zonedev.minapp.util.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Suite de pruebas de integración para el flujo de autenticación.
 * Esta clase prueba el recorrido completo del usuario, desde que abre la app
 * en la pantalla de login hasta que llega a la pantalla de perfil después de
 * un inicio de sesión exitoso.
 *
 * IMPORTANTE: Para que estas pruebas funcionen, deben ejecutarse en un emulador
 * o dispositivo Android que tenga conexión a internet para poder comunicarse con Firebase.
 */
@RunWith(AndroidJUnit4::class)
class LoginFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * PRUEBA DEL "CAMINO FELIZ" (HAPPY PATH)
     *
     * Objetivo: Verificar que un usuario con credenciales válidas puede iniciar sesión
     * y es redirigido a la pantalla principal (ProfileScreen).
     */
    @Test
    fun loginExitoso_navegaAPantallaDePerfil() {
        // --- PREPARACIÓN ---
        val emailDePrueba = "pruebas@gmail.com"
        val passwordDePrueba = "pruebas1234"

        // Obtenemos los textos VISIBLES desde recursos. Esto sigue siendo una buena práctica.
        val tituloPantallaPerfil = composeTestRule.activity.getString(R.string.Descripcion_Navbar_Icon_Profile_Screen)

        // --- ACCIÓN ---
        // 1. Encontrar el campo de email por su TestTag y escribir en él.
        // REFACTOR: Usamos performTextInput para consistencia.
        composeTestRule.onNodeWithTag(TestTags.EMAIL_FIELD).performTextInput(emailDePrueba)

        // 2. Encontrar el campo de contraseña por su TestTag y escribir en él.
        composeTestRule.onNodeWithTag(TestTags.PASSWORD_FIELD).performTextInput(passwordDePrueba)

        // 3. Encontrar el botón de login por su TestTag y hacer clic.
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        // Esperamos hasta 10 segundos a que aparezca el título de la pantalla de perfil.
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText(tituloPantallaPerfil).fetchSemanticsNodes().isNotEmpty()
        }

        // 4. Afirmar que el título de la pantalla de perfil está visible.
        composeTestRule.onNodeWithText(tituloPantallaPerfil).assertIsDisplayed()
    }

    /**
     * PRUEBA DEL "CAMINO TRISTE" (SAD PATH)
     *
     * Objetivo: Verificar que si el usuario introduce credenciales incorrectas,
     * se muestra un modal de error y permanece en la pantalla de login.
     */
    @Test
    fun loginFallido_credencialesIncorrectas_muestraErrorYPermaneceEnLogin() {
        // --- PREPARACIÓN ---
        val emailInvalido = "usuario-no-existe@example.com"
        val passwordInvalida = "passwordincorrecta"

        // Textos de la UI que se van a verificar
        val tituloModalError = composeTestRule.activity.getString(R.string.Title_Error)
        val mensajeModalError = composeTestRule.activity.getString(R.string.Parametros_Incorrectos_Login)

        // --- ACCIÓN ---
        // 1. Escribir en los campos de texto usando los TestTags.
        composeTestRule.onNodeWithTag(TestTags.EMAIL_FIELD).performTextInput(emailInvalido)
        composeTestRule.onNodeWithTag(TestTags.PASSWORD_FIELD).performTextInput(passwordInvalida)

        // 2. Hacer clic en el botón de login.
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).performClick()

        // --- VERIFICACIÓN ---
        // 3. Esperar y afirmar que el modal de error se muestra.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(tituloModalError).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(tituloModalError).assertIsDisplayed()
        composeTestRule.onNodeWithText(mensajeModalError).assertIsDisplayed()

        // 4. Aserción adicional para confirmar que no navegamos a otra pantalla.
        // Verificamos que el botón de login sigue presente en la jerarquía de la UI.
        composeTestRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsDisplayed()
    }
}
