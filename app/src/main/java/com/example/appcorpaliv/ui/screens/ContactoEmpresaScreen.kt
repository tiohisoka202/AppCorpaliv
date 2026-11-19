package com.example.appcorpaliv.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcorpaliv.network.RetrofitClient
import com.example.appcorpaliv.network.SolicitudColaboracionDto
import com.example.appcorpaliv.util.NetworkUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoEmpresaScreen(
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombreEmpresa by remember { mutableStateOf("") }
    var emailContacto by remember { mutableStateOf("") }
    var telefonoContacto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var aceptaAlianzaRSE by remember { mutableStateOf(false) }
    var requiereCapacitacion by remember { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }
    var idSolicitudCreada by remember { mutableStateOf<Long?>(null) }

    // Estado del Recurso Nativo 2 (Conectividad)
    var tieneInternet by remember { mutableStateOf(true) }

    val formularioValido = nombreEmpresa.isNotBlank() &&
            (emailContacto.isNotBlank() || telefonoContacto.isNotBlank())

    // Diálogo de confirmación con el ID retornado por ms-vinculacion
    if (idSolicitudCreada != null) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Solicitud Registrada",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "La solicitud de alianza para $nombreEmpresa fue registrada exitosamente en el servidor (ID: #$idSolicitudCreada)."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        idSolicitudCreada = null
                        onVolverClick()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vinculación Empresarial",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver al inicio"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Formulario de Solicitud de Alianza / RSE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Completa los datos de tu organización para coordinar programas de inclusión laboral o capacitaciones con Alba Lab.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = nombreEmpresa,
                onValueChange = { nombreEmpresa = it },
                label = { Text("Nombre de la Empresa o Institución *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = emailContacto,
                onValueChange = { emailContacto = it },
                label = { Text("Correo Corporativo de Contacto *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = telefonoContacto,
                onValueChange = { telefonoContacto = it },
                label = { Text("Teléfono de Contacto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Propuesta o Consulta de Alianza") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Tarjeta agrupando las opciones de colaboración
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Áreas de interés",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿Interesado en Programa RSE / Inclusión?",
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = aceptaAlianzaRSE,
                            onCheckedChange = { aceptaAlianzaRSE = it }
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = requiereCapacitacion,
                            onCheckedChange = { requiereCapacitacion = it }
                        )
                        Text(
                            text = "Solicitar capacitación sobre discapacidad múltiple",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !tieneInternet,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "No se detecta conexión activa a internet.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    tieneInternet = NetworkUtils.isInternetAvailable(context)

                    scope.launch {
                        enviando = true
                        try {
                            val tipo = when {
                                aceptaAlianzaRSE && requiereCapacitacion -> "ALIANZA_RSE_Y_CAPACITACION"
                                aceptaAlianzaRSE -> "ALIANZA_RSE"
                                requiereCapacitacion -> "CAPACITACION"
                                else -> "CONSULTA_GENERAL"
                            }

                            val nuevaSolicitud = SolicitudColaboracionDto(
                                nombreContacto = nombreEmpresa,
                                rutOOrganizacion = nombreEmpresa,
                                email = emailContacto.ifBlank { null },
                                telefono = telefonoContacto.ifBlank { null },
                                tipoColaboracion = tipo,
                                mensaje = mensaje
                            )

                            val response = RetrofitClient.vinculacionApi.crearSolicitud(nuevaSolicitud)
                            if (response.isSuccessful) {
                                idSolicitudCreada = response.body()?.id ?: 0L
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al enviar (${response.code()}): verifica los datos",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error de red con ms-vinculacion (9092): ${e.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            enviando = false
                        }
                    }
                },
                enabled = formularioValido && !enviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Enviando al servidor...", fontSize = 15.sp)
                } else {
                    Text(
                        text = "Enviar Solicitud de Contacto",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            OutlinedButton(
                onClick = onVolverClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver al Inicio", fontSize = 15.sp)
            }
        }
    }
}