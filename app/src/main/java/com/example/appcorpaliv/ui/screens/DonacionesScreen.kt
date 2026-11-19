package com.example.appcorpaliv.ui.screens

import android.util.Patterns
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
fun DonacionesScreen(
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val montosRapidos = listOf("5000", "10000", "20000")
    var montoSeleccionado by remember { mutableStateOf("10000") }
    var montoPersonalizado by remember { mutableStateOf("") }

    var nombreDonante by remember { mutableStateOf("") }
    var emailDonante by remember { mutableStateOf("") }
    var telefonoDonante by remember { mutableStateOf("") }
    var mensajeApoyo by remember { mutableStateOf("") }

    var enviando by remember { mutableStateOf(false) }
    var idDonacionCreada by remember { mutableStateOf<Long?>(null) }
    var tieneInternet by remember { mutableStateOf(true) }

    val montoFinal = if (montoSeleccionado == "OTRO") montoPersonalizado else montoSeleccionado

    // Validaciones
    val emailValido = emailDonante.isNotBlank() &&
            Patterns.EMAIL_ADDRESS.matcher(emailDonante.trim()).matches()
    val mostrarErrorEmail = emailDonante.isNotBlank() && !emailValido

    val telefonoValido = telefonoDonante.isBlank() || telefonoDonante.length >= 8
    val mostrarErrorTelefono = telefonoDonante.isNotBlank() && !telefonoValido

    val formularioValido = nombreDonante.trim().length >= 3 &&
            emailValido &&
            telefonoValido &&
            montoFinal.isNotBlank() && (montoFinal.toIntOrNull() ?: 0) > 0

    // Diálogo de confirmación con los datos de transferencia
    if (idDonacionCreada != null) {
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
                    text = "¡Gracias por tu aporte!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tu compromiso de donación por $$montoFinal CLP quedó registrado (ID: #$idDonacionCreada). Enviaremos el comprobante a ${emailDonante.trim()}."
                    )
                    Text(
                        text = "Para completarlo, realiza la transferencia a:\n" +
                                "• Banco: BancoEstado\n" +
                                "• Tipo: Cuenta Corriente\n" +
                                "• N° Cuenta: 000-12345678\n" +
                                "• RUT: 65.123.456-7\n" +
                                "• Correo: donaciones@corpaliv.cl",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        idDonacionCreada = null
                        onVolverClick()
                    }
                ) {
                    Text("Finalizar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aportes y Donaciones",
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
                text = "Apoya la inclusión en Alba Lab",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tu aporte permite financiar talleres, materiales adaptados y herramientas tecnológicas para personas con discapacidades múltiples.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Tarjeta con datos de transferencia directa
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Datos de Transferencia CORPALIV",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(
                                    AnnotatedString("CORPALIV | RUT: 65.123.456-7 | BancoEstado Cta Cte: 000-12345678 | donaciones@corpaliv.cl")
                                )
                                Toast.makeText(context, "Datos bancarios copiados", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiar datos bancarios"
                            )
                        }
                    }
                    Text("BancoEstado • Cuenta Corriente N° 000-12345678", fontSize = 13.sp)
                    Text("RUT: 65.123.456-7 • donaciones@corpaliv.cl", fontSize = 13.sp)
                }
            }

            Text(
                text = "Selecciona o ingresa el monto (CLP) *",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Botones rápidos de monto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                montosRapidos.forEach { monto ->
                    FilterChip(
                        selected = montoSeleccionado == monto,
                        onClick = { montoSeleccionado = monto },
                        label = { Text("$$monto") },
                        modifier = Modifier.weight(1f)
                    )
                }
                FilterChip(
                    selected = montoSeleccionado == "OTRO",
                    onClick = { montoSeleccionado = "OTRO" },
                    label = { Text("Otro") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (montoSeleccionado == "OTRO") {
                OutlinedTextField(
                    value = montoPersonalizado,
                    onValueChange = { soloNumeros ->
                        montoPersonalizado = soloNumeros.filter { it.isDigit() }
                    },
                    label = { Text("Monto en pesos (CLP) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = nombreDonante,
                onValueChange = { nombreDonante = it },
                label = { Text("Nombre completo o Razón Social *") },
                isError = nombreDonante.isNotBlank() && nombreDonante.trim().length < 3,
                supportingText = {
                    if (nombreDonante.isNotBlank() && nombreDonante.trim().length < 3) {
                        Text("Ingresa al menos 3 caracteres")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = emailDonante,
                onValueChange = { emailDonante = it },
                label = { Text("Correo electrónico (para comprobante) *") },
                isError = mostrarErrorEmail,
                supportingText = {
                    if (mostrarErrorEmail) {
                        Text("Ingresa un correo válido (ej: nombre@correo.cl)")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = telefonoDonante,
                onValueChange = { soloDigitos ->
                    telefonoDonante = soloDigitos.filter { it.isDigit() || it == '+' }
                },
                label = { Text("Teléfono de contacto (opcional)") },
                isError = mostrarErrorTelefono,
                supportingText = {
                    if (mostrarErrorTelefono) {
                        Text("El teléfono debe tener al menos 8 dígitos")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = mensajeApoyo,
                onValueChange = { mensajeApoyo = it },
                label = { Text("Mensaje de apoyo (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

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

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    tieneInternet = NetworkUtils.isInternetAvailable(context)

                    scope.launch {
                        enviando = true
                        try {
                            val detalleMensaje = buildString {
                                append("Monto comprometido: $$montoFinal CLP.")
                                if (mensajeApoyo.isNotBlank()) {
                                    append(" Mensaje: ${mensajeApoyo.trim()}")
                                }
                            }

                            val nuevaDonacion = SolicitudColaboracionDto(
                                nombreContacto = nombreDonante.trim(),
                                rutOOrganizacion = "DONANTE_PARTICULAR",
                                email = emailDonante.trim(),
                                telefono = telefonoDonante.ifBlank { null },
                                tipoColaboracion = "DONACION",
                                mensaje = detalleMensaje
                            )

                            val response = RetrofitClient.vinculacionApi.crearSolicitud(nuevaDonacion)
                            if (response.isSuccessful) {
                                idDonacionCreada = response.body()?.id ?: 0L
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al registrar (${response.code()}): verifica los datos",
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
                    Text("Registrando aporte...", fontSize = 15.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar Donación",
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