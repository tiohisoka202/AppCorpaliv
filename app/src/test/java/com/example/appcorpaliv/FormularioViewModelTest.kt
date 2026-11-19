package com.example.appcorpaliv

import android.content.Context
import android.util.Log
import com.example.appcorpaliv.data.AppDatabase
import com.example.appcorpaliv.data.BeneficiarioDao
import com.example.appcorpaliv.network.RetrofitClient
import com.example.appcorpaliv.network.VinculacionApiService
import com.example.appcorpaliv.viewmodel.FormularioViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FormularioViewModelTest {

    private lateinit var viewModel: FormularioViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FormularioViewModel()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun estadoInicialDebeSerInvalido() {
        val state = viewModel.uiState.value
        assertFalse(state.esValido)
        assertEquals("", state.nombre)
        assertEquals("", state.rut)
        assertEquals("", state.email)
        assertEquals("", state.telefono)
        assertEquals("", state.tipoDiscapacidad)
    }

    @Test
    fun cuandoNombreEstaVacioDebeMostrarError() {
        viewModel.onNombreChanged("")
        val state = viewModel.uiState.value

        assertEquals("El nombre es obligatorio", state.errorNombre)
        assertFalse(state.esValido)
    }

    @Test
    fun cuandoNombreEsValidoDebeLimpiarError() {
        viewModel.onNombreChanged("")
        viewModel.onNombreChanged("María González")
        val state = viewModel.uiState.value

        assertNull(state.errorNombre)
        assertEquals("María González", state.nombre)
    }

    @Test
    fun cuandoRutEsCortoDebeMarcarError() {
        viewModel.onRutChanged("12345")
        val state = viewModel.uiState.value

        assertEquals("Ingrese un RUT válido", state.errorRut)
        assertFalse(state.esValido)
    }

    @Test
    fun cuandoCorreoEsInvalidoDebeMarcarError() {
        viewModel.onEmailChanged("correo_invalido")
        val state = viewModel.uiState.value

        assertEquals("Formato de correo inválido", state.errorEmail)
        assertFalse(state.esValido)
    }

    @Test
    fun cuandoImagenUriCambiaDebeActualizarEstado() {
        val uriPrueba = "content://media/external/images/media/101"
        viewModel.onImagenUriChanged(uriPrueba)

        assertEquals(uriPrueba, viewModel.uiState.value.imagenUri)
    }

    @Test
    fun cuandoTodosLosCamposSonValidosEsValidoDebeSerTrue() {
        viewModel.onNombreChanged("Juan Pérez")
        viewModel.onRutChanged("12345678-9")
        viewModel.onEmailChanged("juan.perez@corpaliv.cl")
        viewModel.onTelefonoChanged("+56912345678")
        viewModel.onTipoDiscapacidadChanged("Visual")

        val state = viewModel.uiState.value

        assertNull(state.errorNombre)
        assertNull(state.errorRut)
        assertNull(state.errorEmail)
        assertTrue(state.esValido)
    }

    @Test
    fun cuandoFormularioNoEsValidoGuardarNoDebeEjecutarse() = runTest {
        val mockContext = mockk<Context>(relaxed = true)
        viewModel.guardarBeneficiario(mockContext)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.guardadoExitoso)
    }

    @Test
    fun cuandoFormularioEsValidoGuardarDebePersistirEnRoomYSincronizarApi() = runTest {
        val mockContext = mockk<Context>(relaxed = true)
        val mockDb = mockk<AppDatabase>()
        val mockDao = mockk<BeneficiarioDao>()
        val mockApi = mockk<VinculacionApiService>(relaxed = true)

        mockkObject(AppDatabase)
        every { AppDatabase.getDatabase(mockContext) } returns mockDb
        every { mockDb.beneficiarioDao() } returns mockDao
        coEvery { mockDao.insertarBeneficiario(any()) } returns Unit

        mockkObject(RetrofitClient)
        every { RetrofitClient.vinculacionApi } returns mockApi

        viewModel.onNombreChanged("Juan Pérez")
        viewModel.onRutChanged("12345678-9")
        viewModel.onEmailChanged("juan.perez@corpaliv.cl")
        viewModel.onTelefonoChanged("+56912345678")
        viewModel.onTipoDiscapacidadChanged("Visual")
        viewModel.onImagenUriChanged("content://foto.jpg")

        viewModel.guardarBeneficiario(mockContext)
        advanceUntilIdle()

        coVerify(exactly = 1) { mockDao.insertarBeneficiario(any()) }
        coVerify(exactly = 1) { mockApi.crearSolicitud(any()) }
        assertTrue(viewModel.uiState.value.guardadoExitoso)
    }

    @Test
    fun cuandoApiFallaDebeCapturarExcepcionYMarcarGuardadoExitosoIgual() = runTest {
        val mockContext = mockk<Context>(relaxed = true)
        val mockDb = mockk<AppDatabase>()
        val mockDao = mockk<BeneficiarioDao>()
        val mockApi = mockk<VinculacionApiService>()

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        mockkObject(AppDatabase)
        every { AppDatabase.getDatabase(mockContext) } returns mockDb
        every { mockDb.beneficiarioDao() } returns mockDao
        coEvery { mockDao.insertarBeneficiario(any()) } returns Unit

        mockkObject(RetrofitClient)
        every { RetrofitClient.vinculacionApi } returns mockApi
        coEvery { mockApi.crearSolicitud(any()) } throws RuntimeException("Error de red")

        viewModel.onNombreChanged("Ana Torres")
        viewModel.onRutChanged("98765432-1")
        viewModel.onEmailChanged("ana@corpaliv.cl")
        // Dejamos teléfono y discapacidad vacíos para cubrir también las ramas .ifBlank

        viewModel.guardarBeneficiario(mockContext)
        advanceUntilIdle()

        coVerify(exactly = 1) { mockDao.insertarBeneficiario(any()) }
        assertTrue(viewModel.uiState.value.guardadoExitoso)
    }
}