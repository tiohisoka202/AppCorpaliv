package com.example.appcorpaliv.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appcorpaliv.navigation.Screen
import com.example.appcorpaliv.ui.components.ImagenInteligente
import com.example.appcorpaliv.viewmodel.FormularioViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    navController: NavController,
    viewModel: FormularioViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var enviando by remember { mutableStateOf(false) }

    // Launcher Galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImagenUriChanged(uri?.toString())
    }

    // Helper para generar URI temporal de la cámara
    fun crearUriCamara(): Uri {
        val file = File.createTempFile("foto_beneficiario_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    // Launcher Cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito && tempCameraUri != null) {
            viewModel.onImagenUriChanged(tempCameraUri.toString())
        }
    }

    // Launcher para solicitar Permiso Nativo de Cámara dinámicamente
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = crearUriCamara()
            tempCameraUri = uri
            camaraLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                "Se requiere permiso de la cámara para tomar fotos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Diálogo de confirmación cuando Room y Retrofit terminan de guardar
    if (state.guardadoExitoso) {
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
                    text = "Registro Completado",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Los datos de ${state.nombre} (RUT: ${state.rut}) se guardaron localmente y se enviaron a CORPALIV."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        enviando = false
                        navController.navigate(Screen.Catalogo.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                ) {
                    Text("Ver Servicios")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        enviando = false
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("Ir al Inicio")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registro de Beneficiario",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta de fotografía / documento
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImagenInteligente(imagenUri = state.imagenUri?.let { Uri.parse(it) })

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Galería")
                        }

                        Button(onClick = {
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {
                                val uri = crearUriCamara()
                                tempCameraUri = uri
                                camaraLauncher.launch(uri)
                            } else {
                                permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cámara")
                        }

                        if (state.imagenUri != null) {
                            IconButton(onClick = { viewModel.onImagenUriChanged(null) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Quitar foto",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Campo Nombre con supportingText nativo
            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre Completo *") },
                isError = state.errorNombre != null,
                supportingText = {
                    state.errorNombre?.let { Text(text = it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Campo RUT con supportingText nativo
            OutlinedTextField(
                value = state.rut,
                onValueChange = { viewModel.onRutChanged(it) },
                label = { Text("RUT (ej: 12345678-9) *") },
                isError = state.errorRut != null,
                supportingText = {
                    state.errorRut?.let { Text(text = it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Email con supportingText nativo
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = { Text("Correo Electrónico *") },
                isError = state.errorEmail != null,
                supportingText = {
                    state.errorEmail?.let { Text(text = it) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Teléfono
            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.onTelefonoChanged(it) },
                label = { Text("Teléfono de Contacto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Discapacidad
            OutlinedTextField(
                value = state.tipoDiscapacidad,
                onValueChange = { viewModel.onTipoDiscapacidadChanged(it) },
                label = { Text("Tipo de Discapacidad / Necesidad de Apoyo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Botón de guardado con estado de carga
            Button(
                onClick = {
                    enviando = true
                    viewModel.guardarBeneficiario(context)
                },
                enabled = state.esValido && !enviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (enviando && !state.guardadoExitoso) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Guardando registro...", fontSize = 15.sp)
                } else {
                    Text(
                        text = "Guardar y Continuar a Servicios",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}