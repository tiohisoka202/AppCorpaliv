package com.example.appcorpaliv.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

    // Launcher Galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImagenUriChanged(uri?.toString())
    }

    // Launcher Cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito && tempCameraUri != null) {
            viewModel.onImagenUriChanged(tempCameraUri.toString())
        }
    }

    fun crearUriCamara(): Uri {
        val file = File.createTempFile("foto_beneficiario_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Beneficiario CORPALIV") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImagenInteligente(imagenUri = state.imagenUri?.let { Uri.parse(it) })

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Galería")
                }

                Button(onClick = {
                    val uri = crearUriCamara()
                    tempCameraUri = uri
                    camaraLauncher.launch(uri)
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cámara")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // Campo Nombre + Mensaje Animado
            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre Completo *") },
                isError = state.errorNombre != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AnimatedVisibility(visible = state.errorNombre != null, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = state.errorNombre ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo RUT + Mensaje Animado
            OutlinedTextField(
                value = state.rut,
                onValueChange = { viewModel.onRutChanged(it) },
                label = { Text("RUT (ej: 12345678-9) *") },
                isError = state.errorRut != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AnimatedVisibility(visible = state.errorRut != null, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = state.errorRut ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo Email + Mensaje Animado
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = { Text("Correo Electrónico *") },
                isError = state.errorEmail != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AnimatedVisibility(visible = state.errorEmail != null, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = state.errorEmail ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo Teléfono
            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.onTelefonoChanged(it) },
                label = { Text("Teléfono de Contacto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo Discapacidad
            OutlinedTextField(
                value = state.tipoDiscapacidad,
                onValueChange = { viewModel.onTipoDiscapacidadChanged(it) },
                label = { Text("Tipo de Discapacidad / Necesidad de Apoyo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón que solo se habilita si el formulario es válido
            Button(
                onClick = {
                    viewModel.guardarBeneficiario()
                    navController.navigate(Screen.Catalogo.route)
                },
                enabled = state.esValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar y Continuar a Servicios")
            }
        }
    }
}