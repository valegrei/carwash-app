package pe.com.carwashperuapp.carwashapp.network

import android.icu.math.BigDecimal
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import pe.com.carwashperuapp.carwashapp.network.request.*
import pe.com.carwashperuapp.carwashapp.network.response.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

//const val BASE_URL = "http://192.168.100.9:3000"
//const val BASE_URL = "http://192.168.100.9"
const val BASE_URL = "https://www.carwashperuapp.com"

object BigDecimalAdapter {
    @FromJson
    fun fromJson(string: String) = BigDecimal(string)

    @ToJson
    fun toJson(value: BigDecimal) = value.toString()
}

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .add(BigDecimalAdapter)
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
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespUsuarios

    @POST("api/admin/usuarios")
    suspend fun agregarAdmin(
        @Body reqAddAdmin: ReqAddAdmin,
        @Header("Authorization") authToken: String
    ): Response

    /*@GET("api/usuarios/account")
    suspend fun obtenerUsuario(
        @Path("id") idUsuario: Int,
        @Header("Authorization") authToken: String
    ): RespUsuario*/

    @Multipart
    @PUT("api/usuarios/account")
    suspend fun actualizarUsuario(
        @Part("nombres") nombres: RequestBody,
        @Part("apellidoPaterno") apellidoPaterno: RequestBody,
        @Part("apellidoMaterno") apellidoMaterno: RequestBody,
        @Part("razonSocial") razonSocial: RequestBody,
        @Part("idTipoDocumento") idTipoDocumento: RequestBody,
        @Part("nroDocumento") nroDocumento: RequestBody,
        @Part("nroCel1") nroCel1: RequestBody,
        @Part("nroCel2") nroCel2: RequestBody,
        @Part("acercaDe") acercaDe: RequestBody?,
        @Part foto: MultipartBody.Part?,
        @Part("borrarFoto") borrarFoto: RequestBody?,
        @Header("Authorization") authToken: String
    ): RespUsuario

    @PUT("api/usuarios/pass")
    suspend fun cambiarClaveUsu(
        @Body reqCambiarClaveUsu: ReqCambiarClaveUsu,
        @Header("Authorization") authToken: String
    ): Response

    /* ADMIN */

    @PUT("api/admin/usuarios/{id}")
    suspend fun modificarUsuario(
        @Path("id") idUsuario: Int,
        @Body usuario: Usuario,
        @Header("Authorization") authToken: String
    ): Response

    @PUT("api/admin/usuarios/{id}/password")
    suspend fun cambiarClave(
        @Path("id") idUsuario: Int,
        @Body cambiarClaveAdmin: ReqCambiarClaveAdmin,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/admin/anuncios")
    suspend fun obtenerAnuncios(
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespAnuncio

    @Multipart
    @POST("api/admin/anuncios")
    suspend fun crearAnuncio(
        @Part("descripcion") descripcion: RequestBody?,
        @Part("url") url: RequestBody?,
        @Part("mostrar") mostrar: RequestBody?,
        @Part imagen: MultipartBody.Part?,
        @Header("Authorization") authToken: String
    ): Response


    @Multipart
    @PUT("api/admin/anuncios/{id}")
    suspend fun actualizarAnuncio(
        @Path("id") id: Int,
        @Part("descripcion") descripcion: RequestBody?,
        @Part("url") url: RequestBody?,
        @Part("mostrar") mostrar: RequestBody?,
        @Part imagen: MultipartBody.Part?,
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
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespParams
    @GET("api/admin/reserva")
    suspend fun obtenerReservasAdmin(
        @Query("fecha") fecha: String?,
        @Query("fechaFin") fechaFin: String?,
        @Query("filtroDis") filtroDis: String?,
        @Query("filtroCli") filtroCli: String?,
        @Header("Authorization") authToken: String
    ): RespReservas

    @PUT("api/admin/reserva/{idReserva}")
    suspend fun atenderReservaAdmin(
        @Path("idReserva") idReserva: Int,
        @Body reqAtenderReserva: ReqAtenderReserva,
        @Header("Authorization") authToken: String
    ): Response


    /* Distribuidor */

    @GET("api/distrib/servicio")
    suspend fun obtenerServicios(
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespServicios

    @POST("api/distrib/servicio")
    suspend fun agregarServicio(
        @Body reqAddServicio: ReqAddServicio,
        @Header("Authorization") authToken: String
    ): Response

    @PUT("api/distrib/servicio")
    suspend fun modificarServicio(
        @Body reqModServicio: ReqModServicio,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/usuarios/direccion")
    suspend fun obtenerDirecciones(
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespDireccion

    @POST("api/usuarios/direccion")
    suspend fun agregarDireccion(
        @Body reqDireccion: ReqDireccion,
        @Header("Authorization") authToken: String
    ): Response

    @PUT("api/usuarios/direccion/{idDireccion}")
    suspend fun modificarDireccion(
        @Path("idDireccion") idDireccion: Int,
        @Body reqDireccion: ReqDireccion,
        @Header("Authorization") authToken: String
    ): Response

    @DELETE("api/usuarios/direccion/{idDireccion}")
    suspend fun eliminarDireccion(
        @Path("idDireccion") idDireccion: Int,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/distrib/horarioConfig")
    suspend fun obtenerHorarioConfigs(
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespHorarioConfig

    @POST("api/distrib/horarioConfig")
    suspend fun agregarHorarioConfig(
        @Body reqHorarioConfig: ReqHorarioConfig,
        @Header("Authorization") authToken: String
    ): Response

    @PUT("api/distrib/horarioConfig/{idHorarioConfig}")
    suspend fun modificarHorarioConfig(
        @Path("idHorarioConfig") idHorarioConfig: Int,
        @Body reqHorarioConfig: ReqHorarioConfig,
        @Header("Authorization") authToken: String
    ): Response

    @DELETE("api/distrib/horarioConfig/{idHorarioConfig}")
    suspend fun eliminarHorarioConfig(
        @Path("idHorarioConfig") idHorarioConfig: Int,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/distrib/reserva")
    suspend fun obtenerReservasDistrib(
        @Query("fecha") fecha: String?,
        @Query("fechaFin") fechaFin: String?,
        @Header("Authorization") authToken: String
    ): RespReservas

    @PUT("api/distrib/reserva/{idReserva}")
    suspend fun atenderReserva(
        @Path("idReserva") idReserva: Int,
        @Body reqAtenderReserva: ReqAtenderReserva,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/clientes/vehiculos")
    suspend fun obtenerVehiculos(
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespVehiculos

    @Multipart
    @POST("api/clientes/vehiculos")
    suspend fun agregarVehiculo(
        @Part("marca") marca: RequestBody?,
        @Part("modelo") modelo: RequestBody?,
        @Part("year") year: RequestBody?,
        @Part("placa") placa: RequestBody?,
        @Part imagen: MultipartBody.Part?,
        @Header("Authorization") authToken: String
    ): Response

    @Multipart
    @PUT("api/clientes/vehiculos/{idVehiculo}")
    suspend fun modificarVehiculo(
        @Path("idVehiculo") idVehiculo: Int,
        @Part("marca") marca: RequestBody?,
        @Part("modelo") modelo: RequestBody?,
        @Part("year") year: RequestBody?,
        @Part("placa") placa: RequestBody?,
        @Part("borrarFoto") borrarFoto: RequestBody?,
        @Part imagen: MultipartBody.Part?,
        @Header("Authorization") authToken: String
    ): Response

    @DELETE("api/clientes/vehiculos/{idVehiculo}")
    suspend fun eliminarVehiculo(
        @Path("idVehiculo") idVehiculo: Int,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/clientes/locales")
    suspend fun obtenerLocales(
        @Query("latNE") latNE: String,
        @Query("longNE") longNE: String,
        @Query("latSW") latSW: String,
        @Query("longSW") longSW: String,
        @Header("Authorization") authToken: String
    ): RespLocales

    @GET("api/clientes/horarios")
    suspend fun obtenerHorarios(
        @Query("idLocal") idLocal: Int,
        @Query("fecha") fecha: String,
        @Query("fechaHora") fechaHora: String,
        @Header("Authorization") authToken: String
    ): RespHorarios

    @POST("api/clientes/reserva")
    suspend fun crearReserva(
        @Body reqReservar: ReqReservar,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/clientes/reserva")
    suspend fun obtenerReservas(
        @Query("fecha") fecha: String?,
        @Query("fechaFin") fechaFin: String?,
        @Header("Authorization") authToken: String
    ): RespReservas

    @DELETE("api/clientes/reserva/{idReserva}")
    suspend fun eliminarReserva(
        @Path("idReserva") idReserva: Int,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/clientes/favoritos")
    suspend fun obtenerLocalesFavoritos(
        @Header("Authorization") authToken: String
    ): RespLocales

    @POST("api/clientes/favoritos")
    suspend fun agregarFavorito(
        @Body reqFavorito: ReqFavorito,
        @Header("Authorization") authToken: String
    ): RespFavorito

    @DELETE("api/clientes/favoritos/{idFavorito}")
    suspend fun eliminarFavorito(
        @Path("idFavorito") idFavorito: Int,
        @Header("Authorization") authToken: String
    ): Response

    @GET("api/clientes/anuncios")
    suspend fun obtenerAnunciosCli(
        @Query("lastSincro") lastSincro: String?,
        @Header("Authorization") authToken: String
    ): RespAnuncio
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