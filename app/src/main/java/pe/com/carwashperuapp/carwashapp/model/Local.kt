package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Local(
    @Json(name = "id") val id: Int,
    @Json(name = "departamento") val departamento: String?,
    @Json(name = "provincia") val provincia: String?,
    @Json(name = "distrito") val distrito: String?,
    @Json(name = "ubigeo") val ubigeo: String?,
    @Json(name = "direccion") val direccion: String,
    @Json(name = "latitud") val latitud: String?,
    @Json(name = "longitud") val longitud: String?,
    @Json(name = "estado") val estado: Boolean?,
    @Json(name = "idUsuario") val idUsuario: Int?,
    @Json(name = "Usuario") val distrib: Distrib? = null,
    @Json(name = "Favoritos") val favoritos: List<Favorito>? = null,
    @Json(name = "HorarioConfig") val horario: HorarioLocal? = null,
) {
    fun getRazSocDireccion(): String = "${distrib?.razonSocial ?: ""}\n$direccion"
}