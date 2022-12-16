package pe.com.valegrei.carwashapp.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.network.request.*
import pe.com.valegrei.carwashapp.network.response.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

const val BASE_URL = "http://192.168.100.9:3000"
//const val BASE_URL = "https://www.carwashperuapp.com"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    /* Rutas de Autorizacion */
    @POST("auth/login")
    suspend fun iniciarSesion(@Body reqLogin: ReqLogin): RespLogin

    @POST("auth/signup")
    suspend fun registrar(@Body nuevoUsuario: Usuario): RespLogin

    @POST("auth/verify")
    suspend fun verificarCorreo(@Body reqVerificarCorreo: ReqVerificarCorreo): RespLogin

    @POST("auth/verify/code")
    suspend fun solicitarCodigoVerificacion(@Body reqId: ReqId): Response

    @POST("auth/renew/code")
    suspend fun solicitarCodigoNuevaClave(@Body reqCorreo: ReqCorreo): Response

    @POST("auth/renew")
    suspend fun cambiarClave(@Body reqCambiarClave: ReqCambiarClave): RespLogin

    /* Rutas de REST Api */
    /* Usuarios */
    @GET("api/admin/usuarios")
    suspend fun obtenerUsuarios(
        @Query("lastSincro") lastSincro: Date,
        @Header("Authorization") authToken: String
    ): RespUsuarios

    @POST("api/admin/usuarios")
    suspend fun agregarAdmin(
        @Body reqAddAdmin: ReqAddAdmin,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(
        @Path("id") idUsuario: Int,
        @Header("Authorization") authToken: String
    ): RespUsuario

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") idUsuario: Int,
        @Body usuario: Usuario,
        @Header("Authorization") authToken: String
    ): RespUsuario

    @PUT("api/admin/usuarios/{id}")
    suspend fun modificarUsuario(
        @Path("id") idUsuario: Int,
        @Body usuario: Usuario,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/admin/anuncios")
    suspend fun obtenerAnuncios(
        @Query("lastSincro") lastSincro: Date,
        @Header("Authorization") authToken: String
    ): RespAnuncio

    @Multipart
    @POST("api/admin/anuncios")
    suspend fun crearAnuncio(
        @Part("descripcion") descripcion: RequestBody?,
        @Part("url") url: RequestBody?,
        @Part imagen: MultipartBody.Part?,
        @Header("Authorization") authToken: String
    ): Response


    @PUT("api/admin/anuncios/{id}")
    suspend fun actualizarAnuncio(
        @Path("id") descripcion: Int,
        @Body reqAnuncio: ReqAnuncioActualizar,
        @Header("Authorization") authToken: String
    ): Response

    @HTTP(method = "DELETE", path = "api/admin/anuncios/", hasBody = true)
    suspend fun eliminarAnuncios(
        @Body reqIds: ReqAnuncioEliminar,
        @Header("Authorization") authToken: String
    ): Response

    @PUT("api/admin/parametros/smtp")
    suspend fun guardarVerificarSMTP(
        @Body reqParamsSMTP: ReqParamsSMTP,
        @Header("Authorization") authToken: String
    ): Response

    @PUT("api/admin/parametros/correo")
    suspend fun guardarCorreo(
        @Body reqParamsCorreo: ReqParamsCorreo,
        @Header("Authorization") authToken: String
    ): Response

    @POST("api/admin/parametros/correo")
    suspend fun probarCorreo(
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/admin/parametros")
    suspend fun obtenerParametros(
        @Query("lastSincro") lastSincro: Date,
        @Header("Authorization") authToken: String
    ): RespParams
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
                RespError(errorJson)
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
            RespError("Servidor no encontrado")
        is HttpException ->
            extractExceptionMessage(this)!!
        is SocketTimeoutException ->
            RespError("Revisar conexión a Internet")
        else -> {
            if (this.message != null)
                RespError(this.message!!)
            else
                RespError("Error Desconocido")
        }
    }
}