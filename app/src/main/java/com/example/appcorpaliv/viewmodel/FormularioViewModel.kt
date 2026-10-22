package com.example.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioState())
    val uiState: StateFlow<FormularioState> = _uiState.asStateFlow()

    fun onNombreChanged(nuevoNombre: String) {
        val error = if (nuevoNombre.isBlank()) "El nombre es obligatorio" else null
        _uiState.update { it.copy(nombre = nuevoNombre, errorNombre = error) }
        validarFormulario()
    }

    fun onRutChanged(nuevoRut: String) {
        val error = when {
            nuevoRut.isBlank() -> "El RUT es obligatorio"
            nuevoRut.length < 8 -> "Ingrese un RUT válido"
            else -> null
        }
        _uiState.update { it.copy(rut = nuevoRut, errorRut = error) }
        validarFormulario()
    }

    fun onEmailChanged(nuevoEmail: String) {
        val error = when {
            nuevoEmail.isBlank() -> "El correo es obligatorio"
            !nuevoEmail.contains("@") || !nuevoEmail.contains(".") -> "Formato de correo inválido"
            else -> null
        }
        _uiState.update { it.copy(email = nuevoEmail, errorEmail = error) }
        validarFormulario()
    }

    fun onTelefonoChanged(nuevoTelefono: String) {
        _uiState.update { it.copy(telefono = nuevoTelefono) }
    }

    fun onTipoDiscapacidadChanged(nuevoTipo: String) {
        _uiState.update { it.copy(tipoDiscapacidad = nuevoTipo) }
    }

    fun onImagenUriChanged(uriString: String?) {
        _uiState.update { it.copy(imagenUri = uriString) }
    }

    private fun validarFormulario() {
        val state = _uiState.value
        val esValido = state.nombre.isNotBlank() &&
                state.rut.isNotBlank() &&
                state.email.isNotBlank() &&
                state.errorNombre == null &&
                state.errorRut == null &&
                state.errorEmail == null

        _uiState.update { it.copy(esValido = esValido) }
    }

    fun guardarBeneficiario() {
        if (_uiState.value.esValido) {
            _uiState.update { it.copy(guardadoExitoso = true) }
        }
    }
}