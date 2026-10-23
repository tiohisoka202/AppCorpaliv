package com.example.appcorpaliv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcorpaliv.ui.screens.CatalogoScreen
import com.example.appcorpaliv.ui.screens.ContactoEmpresaScreen
import com.example.appcorpaliv.ui.screens.FormularioScreen
import com.example.appcorpaliv.ui.theme.AppCorpalivTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ENVOLVEMOS TODA LA NAVEGACIÓN EN EL TEMA
            AppCorpalivTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "formulario_beneficiarios"
    ) {
        // Formulario 1: Beneficiarios
        composable("formulario_beneficiarios") {
            Box(modifier = Modifier.fillMaxSize()) {
                FormularioScreen(navController = navController)

                Button(
                    onClick = { navController.navigate("contacto_empresas") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("Ir a Contacto Empresas ➔")
                }
            }
        }

        // Formulario 2: Contacto Empresas
        composable("contacto_empresas") {
            ContactoEmpresaScreen(
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // CatalogoScreen configurado con navController
        composable("catalogo_screen") {
            CatalogoScreen(navController = navController)
        }
    }
}