package pe.com.valegrei.carwashapp.database.usuario

import com.squareup.moshi.Json

enum class TipoUsuario(val id: Int, val nombre: String) {
    ADMIN(1, "Administrador"),
    CLIENTE(2, "Cliente"),
    DISTR(3, "Distribuidor")
}

enum class TipoDocumento(val id: Int, val nombre: String, val digitos: Int) {
    DNI(1, "DNI", 8),
    RUC(2, "RUC", 11),
    CEXT(3, "CEXT", 12)
}

data class Usuario(
    @Json(name = "id")
    var id: Int?,
    @Json(name = "correo")
    var correo: String,
    @Json(name = "clave")
    var clave: String?,
    @Json(name = "nombres")
    var nombres: String?,
    @Json(name = "apellidoPaterno")
    var apellidoPaterno: String?,
    @Json(name = "apellidoMaterno")
    var apellidoMaterno: String?,
    @Json(name = "razonSocial")
    var razonSocial: String?,
    @Json(name = "nroDocumento")
    var nroDocumento: String?,
    @Json(name = "nroCel1")
    var nroCel1: String?,
    @Json(name = "nroCel2")
    var nroCel2: String?,
    @Json(name = "distAct")
    var distAct: Boolean,
    @Json(name = "verificado")
    var verificado: Boolean,
    @Json(name = "estado")
    var estado: Boolean,
    @Json(name = "idTipoUsuario")
    var idTipoUsuario: Int,
    @Json(name = "idTipoDocumento")
    var idTipoDocumento: Int,
    @Json(name = "createdAt")
    var createdAt: String?,
    @Json(name = "updatedAt")
    var updatedAt: String?,
) {
    constructor() : this(
        0, "", "", "", "", "",
        "", "", "", "", false, false, false,
        0, 0, "", ""
    )
}