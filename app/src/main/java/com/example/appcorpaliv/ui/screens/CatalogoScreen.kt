package com.example.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appcorpaliv.navigation.Screen
import com.example.appcorpaliv.network.RetrofitClient
import com.example.appcorpaliv.network.ServicioInclusionDto
import kotlinx.coroutines.launch

private data class DetalleEnriquecido(
    val explicacionCompleta: String,
    val dirigidoA: String,
    val modalidad: String
)

private fun obtenerDetallePorServicio(servicio: ServicioInclusionDto): DetalleEnriquecido {
    val tituloLower = servicio.titulo.lowercase()
    return when {
        "multisensorial" in tituloLower -> DetalleEnriquecido(
            explicacionCompleta = "Sesiones terapéuticas personalizadas en salas equipadas con tecnología táctil, lumínica y sonora. Buscan fomentar la comunicación, la percepción del entorno y la autonomía funcional mediante estímulos controlados.",
            dirigidoA = "Niños, jóvenes y adultos con discapacidad múltiple y sordoceguera.",
            modalidad = "Presencial en sedes CORPALIV · Sesiones semanales."
        )
        "laboral" in tituloLower -> DetalleEnriquecido(
            explicacionCompleta = "Programa puente entre personas con discapacidad y empresas comprometidas con la Ley de Inclusión. Incluye análisis de puestos de trabajo, ajustes razonables, apresto laboral y seguimiento continuo en el entorno empresarial.",
            dirigidoA = "Participantes en etapa laboral y organizaciones empleadoras.",
            modalidad = "Híbrida · Acompañamiento directo en empresa."
        )
        "capacitaci" in tituloLower -> DetalleEnriquecido(
            explicacionCompleta = "Talleres prácticos orientados al uso de tecnologías asistivas, comunicación aumentativa y alternativa (CAA), y alfabetización digital accesible tanto para usuarios como para equipos profesionales y docentes.",
            dirigidoA = "Usuarios, cuidadores, profesionales de la educación y empresas.",
            modalidad = "Presencial y online sincrónico · Certificación incluida."
        )
        "familiar" in tituloLower -> DetalleEnriquecido(
            explicacionCompleta = "Acompañamiento psicosocial, talleres de crianza respetuosa, asesoría en derechos y beneficios estatales, y espacios de encuentro entre cuidadores para fortalecer el entorno familiar y comunitario.",
            dirigidoA = "Padres, madres, tutores y redes cercanas de los beneficiarios.",
            modalidad = "Grupal e individual · Presencial y telemática."
        )
        else -> DetalleEnriquecido(
            explicacionCompleta = "${servicio.descripcion} Este servicio forma parte de la red de apoyo integral de CORPALIV / Alba Lab para promover la participación plena y la igualdad de oportunidades.",
            dirigidoA = "Beneficiarios registrados, familias y organizaciones vinculadas.",
            modalidad = "Coordinación directa vía formulario de contacto o atención presencial."
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var servicios by remember { mutableStateOf<List<ServicioInclusionDto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun cargarDesdeBackend() {
        scope.launch {
            cargando = true
            mensajeError = null
            try {
                val response = RetrofitClient.contenidosApi.obtenerContenidos()
                if (response.isSuccessful && response.body() != null) {
                    servicios = response.body()!!
                } else {
                    mensajeError = "Error del servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "No se pudo conectar con ms-contenidos (puerto 9091): ${e.localizedMessage}"
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarDesdeBackend()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Iniciativas y Servicios",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Toca cada tarjeta para expandir o contraer detalles",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { cargarDesdeBackend() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar catálogo"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Volver al Inicio",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                cargando -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Cargando iniciativas desde el servidor...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                mensajeError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = mensajeError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { cargarDesdeBackend() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar conexión")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(servicios) { servicio ->
                            TarjetaServicioBackend(servicio = servicio)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaServicioBackend(servicio: ServicioInclusionDto) {
    var expandida by remember { mutableStateOf(true) }
    val detalle = remember(servicio) { obtenerDetallePorServicio(servicio) }

    val (icono, colorAcento, etiquetaLimpia) = when (servicio.categoria.uppercase()) {
        "ALBA_LAB" -> Triple(Icons.Default.Build, Color(0xFF4FC3F7), "ALBA LAB")
        "CAPACITACION" -> Triple(Icons.Default.School, Color(0xFF81C784), "CAPACITACIÓN")
        else -> Triple(Icons.Default.Favorite, Color(0xFFCE93D8), servicio.categoria.replace("_", " "))
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expandida = !expandida },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colorAcento.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icono,
                            contentDescription = etiquetaLimpia,
                            tint = colorAcento,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorAcento.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = etiquetaLimpia,
                            color = colorAcento,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Icon(
                    imageVector = if (expandida) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expandida) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = servicio.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = servicio.descripcion,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(visible = expandida) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = detalle.explicacionCompleta,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoFilaDetalle(titulo = "Dirigido a:", valor = detalle.dirigidoA)
                    Spacer(modifier = Modifier.height(4.dp))
                    InfoFilaDetalle(titulo = "Modalidad:", valor = detalle.modalidad)
                }
            }
        }
    }
}

@Composable
private fun InfoFilaDetalle(titulo: String, valor: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(15.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$titulo ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = valor,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}