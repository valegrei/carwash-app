package pe.com.carwashperuapp.carwashapp.database.usuario

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.network.BASE_URL

enum class EstadoUsuario(val id: Int, val nombre: String) {
    INACTIVO(0, "Inactivo"),
    ACTIVO(1, "Activo"),
    VERIFICANDO(2, "Verificando")
}

enum class TipoUsuario(val id: Int, val nombre: String) {
    ADMIN(1, "Administrador"),
    CLIENTE(2, "Cliente"),
    DISTR(3, "Distribuidor")
}

enum class TipoDocumento(val id: Int, val nombre: String, val digitos: Int) {
    DNI(1, "DNI", 8),
    RUC(2, "RUC", 11),
    CEXT(3, "CEXT", 12);

    override fun toString(): String = nombre
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "usuario", indices = [Index(value = ["correo"], unique = true)])
data class Usuario(
    @Json(name = "id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int?,
    @Json(name = "correo")
    @ColumnInfo(name = "correo")
    var correo: String,
    @Ignore
    @Json(name = "clave")
    var clave: String?,
    @Json(name = "nombres")
    @ColumnInfo(name = "nombres")
    var nombres: String?,
    @Json(name = "apellidoPaterno")
    @ColumnInfo(name = "ape_paterno")
    var apellidoPaterno: String?,
    @Json(name = "apellidoMaterno")
    @ColumnInfo(name = "ape_materno")
    var apellidoMaterno: String?,
    @Json(name = "razonSocial")
    @ColumnInfo(name = "razon_social")
    var razonSocial: String?,
    @Json(name = "nroDocumento")
    @ColumnInfo(name = "nro_doc")
    var nroDocumento: String?,
    @Json(name = "nroCel1")
    @ColumnInfo(name = "nro_cel_1")
    var nroCel1: String?,
    @Json(name = "nroCel2")
    @ColumnInfo(name = "nro_cel_2")
    var nroCel2: String?,
    @Json(name = "estado")
    @ColumnInfo(name = "estado")
    var estado: Int,
    @Json(name = "idTipoUsuario")
    @ColumnInfo(name = "id_tipo_usuario")
    var idTipoUsuario: Int,
    @Json(name = "idTipoDocumento")
    @ColumnInfo(name = "id_tipo_documento")
    var idTipoDocumento: Int,
    @Json(name = "acercaDe")
    var acercaDe: String? = null,
    @Json(name = "path")
    var path: String? = null
) {
    constructor() : this(
        0, "", "", "", "", "",
        "", "", "", "", 0,
        0, 0
    )

    fun getNombreCompleto(): String {
        val nombres = "${nombres ?: ""} ${apellidoPaterno ?: ""} ${apellidoMaterno ?: ""}"
        if (nombres.trim().isEmpty())
            return "(Nombre incompleto)"
        return nombres
    }

    fun getCorreoNombres(): String {
        return "${correo}\n${getNombreORazSocial()}".trim()
    }

    fun getRazSocial(): String {
        if ((razonSocial ?: "").isEmpty())
            return "(Raz. Social incompleto)"
        return razonSocial!!
    }

    fun getNombreORazSocial(): String {
        return when (idTipoUsuario) {
            TipoUsuario.DISTR.id -> getRazSocial()
            else -> getNombreCompleto()
        }
    }

    fun getRucRazSocial(): String {
        return "${getNroDoc()}\n${getRazSocial()}\n$correo"
    }

    fun getNroDoc(): String {
        if ((nroDocumento ?: "").isEmpty())
            return "(Doc. incompleto)"
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

    fun getTipoPerfilNombre(): String {
        return when (idTipoUsuario) {
            TipoUsuario.ADMIN.id -> TipoUsuario.ADMIN.nombre
            TipoUsuario.DISTR.id -> TipoUsuario.DISTR.nombre
            TipoUsuario.CLIENTE.id -> TipoUsuario.CLIENTE.nombre
            else -> ""
        }
    }

    fun getURLFoto(): String? {
        return if (path.isNullOrEmpty()) null
        else "$BASE_URL$path"
    }
}