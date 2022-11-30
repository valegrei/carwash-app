package pe.com.valegrei.carwashapp.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.network.request.*
import pe.com.valegrei.carwashapp.network.response.RespError
import pe.com.valegrei.carwashapp.network.response.RespLogin
import pe.com.valegrei.carwashapp.network.response.Response
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val BASE_URL = "http://192.168.100.9:3000"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getPhotos] method
 */
interface ApiService {
    /* Rutas de Autorizacion */
    @POST("auth/login")
    suspend fun iniciarSesion(@Body reqLogin: ReqLogin): RespLogin

    @POST("auth/signup")
    suspend fun registrar(@Body nuevoUsuario: Usuario): Response

    @POST("auth/verify")
    suspend fun verificarCorreo(@Body reqVerificarCorreo: ReqVerificarCorreo): RespLogin

    @POST("auth/verify/code")
    suspend fun solicitarCodigoVerificacion(@Body reqId: ReqId): Response

    @POST("auth/renew/code")
    suspend fun solicitarCodigoNuevaClave(@Body reqCorreo: ReqCorreo): Response

    @POST("auth/renew")
    suspend fun cambiarClave(@Body reqCambiarClave: ReqCambiarClave): Response

    /* Rutas de REST Api */
}

/**
 * Singleton de nuestra Rest Api
 */
object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

/**
 * Extrae JSON de Respuesta para codigos de estado diferentes a 200
 */
fun extractExceptionMessage(exception: HttpException): RespError? {
    val adapter: JsonAdapter<RespError> = moshi.adapter(RespError::class.java).lenient()
    exception.response()?.run {
        errorBody()?.let {
            val errorJson = it.string()
            return if (!errorJson.contains("{"))
                RespError(message = errorJson)
            else
                adapter.fromJson(errorJson)
        }
    }
    return null
}

/**
 * Manejador de Excepciones referentes a la Api
 */
fun Throwable.handleThrowable(): RespError {
    return when (this) {
        is UnknownHostException ->
            RespError(message = "Servidor no encontrado")
        is HttpException ->
            extractExceptionMessage(exception = this)!!
        is SocketTimeoutException ->
            RespError(message = "Revisar conexión a Internet")
        else -> {
            if (this.message != null)
                RespError(message = this.message!!)
            else
                RespError(message = "Error Desconocido")
        }
    }
}