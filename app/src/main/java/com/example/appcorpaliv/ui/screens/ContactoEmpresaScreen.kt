package com.example.appcorpaliv.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appcorpaliv.util.NetworkUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoEmpresaScreen(
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current

    var nombreEmpresa by remember { mutableStateOf("") }
    var emailContacto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var aceptaAlianzaRSE by remember { mutableStateOf(false) }
    var requiereCapacitacion by remember { mutableStateOf(false) }

    // Estado del Recurso Nativo 2 (Conectividad)
    var tieneInternet by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vinculación Empresarial - Alba Lab") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Formulario de Solicitud de Alianza / RSE",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = nombreEmpresa,
                onValueChange = { nombreEmpresa = it },
                label = { Text("Nombre de la Empresa o Institución") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = emailContacto,
                onValueChange = { emailContacto = it },
                label = { Text("Correo Corporativo de Contacto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Propuesta o Consulta de Alianza") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Componente Switch (Pauta UI)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Interesado en Programa RSE / Inclusión?", modifier = Modifier.weight(1f))
                Switch(
                    checked = aceptaAlianzaRSE,
                    onCheckedChange = { aceptaAlianzaRSE = it }
                )
            }

            // Componente Checkbox (Pauta UI)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = requiereCapacitacion,
                    onCheckedChange = { requiereCapacitacion = it }
                )
                Text("Solicitar capacitación sobre discapacidad múltiple")
            }

            // Animación de Retroalimentación de Red (Animación + Recurso Nativo 2)
            AnimatedVisibility(
                visible = !tieneInternet,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "⚠️ Advertencia: No se detecta conexión activa a internet.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Verificación en tiempo real con el Recurso Nativo 2
                    tieneInternet = NetworkUtils.isInternetAvailable(context)

                    if (nombreEmpresa.isNotBlank() && emailContacto.isNotBlank()) {
                        Toast.makeText(
                            context,
                            "Solicitud enviada. Estado de Red: ${if (tieneInternet) "Conectado" else "Sin Red"}",
                            Toast.LENGTH_LONG
                        ).show()
                        onVolverClick()
                    } else {
                        Toast.makeText(context, "Por favor completa la información obligatoria", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud de Contacto")
            }

            OutlinedButton(
                onClick = onVolverClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Registro de Beneficiarios")
            }
        }
    }
}