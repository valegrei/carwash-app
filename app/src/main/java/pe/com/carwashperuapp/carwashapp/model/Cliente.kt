package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoDocumento

class Cliente(
    @Json(name = "id") val id: Int,
    @Json(name = "correo") val correo: String? = null,
    @Json(name = "nombres") val nombres: String? = null,
    @Json(name = "apellidoPaterno") val apellidoPaterno: String? = null,
    @Json(name = "apellidoMaterno") val apellidoMaterno: String? = null,
    @Json(name = "nroDocumento") val nroDocumento: String? = null,
    @Json(name = "nroCel1") val nroCel1: String? = null,
    @Json(name = "nroCel2") val nroCel2: String? = null,
    @Json(name = "idTipoDocumento") val idTipoDocumento: Int? = 0,
) {

    fun getNombreCompleto(): String {
        val nombres = "${nombres ?: ""} ${apellidoPaterno ?: ""} ${apellidoMaterno ?: ""}"
        if (nombres.trim().isEmpty()) return "(Nombre incompleto)"
        return nombres
    }

    fun getNroDoc(): String {
        if ((nroDocumento ?: "").isEmpty()) return "(Doc. incompleto)"
        return nroDocumento!!
    }

    fun getNombreDoc(): String {
        return when (idTipoDocumento) {
            TipoDocumento.DNI.id -> TipoDocumento.DNI.nombre
            TipoDocumento.RUC.id -> TipoDocumento.RUC.nombre
            TipoDocumento.CEXT.id -> TipoDocumento.CEXT.nombre
            else -> ""
        }
    }

    fun getNroDocFormateado(): String {
        return when (idTipoDocumento) {
            TipoDocumento.DNI.id -> "${TipoDocumento.DNI.nombre}: ${getNroDoc()}"
            TipoDocumento.RUC.id -> "${TipoDocumento.RUC.nombre}: ${getNroDoc()}"
            TipoDocumento.CEXT.id -> "${TipoDocumento.CEXT.nombre}: ${getNroDoc()}"
            else -> ""
        }
    }
}