package pe.com.valegrei.carwashapp.database.usuario

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

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

@Entity(tableName = "usuario")
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
    @Json(name = "distAct")
    @ColumnInfo(name = "dist_act")
    var distAct: Boolean,
    @Json(name = "verificado")
    @ColumnInfo(name = "verificado")
    var verificado: Boolean,
    @Json(name = "estado")
    @ColumnInfo(name = "estado")
    var estado: Boolean,
    @Json(name = "idTipoUsuario")
    @ColumnInfo(name = "id_tipo_usuario")
    var idTipoUsuario: Int,
    @Json(name = "idTipoDocumento")
    @ColumnInfo(name = "id_tipo_documento")
    var idTipoDocumento: Int,
    @Json(name = "createdAt")
    @ColumnInfo(name = "created_at")
    var createdAt: Date?,
    @Json(name = "updatedAt")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date?,
) {
    constructor() : this(
        0, "", "", "", "", "",
        "", "", "", "", false, false, false,
        0, 0, null, null
    )

    fun getNombreCompleto(): String {
        val nombres = "${nombres?:""} ${apellidoPaterno?:""} ${apellidoMaterno?:""}"
        if(nombres.trim().isEmpty())
            return "(Nombre incompleto)"
        return nombres
    }

    fun getCorreoNombres(): String {
        return "${correo}\n${getNombreORazSocial()}".trim()
    }

    fun getRazSocial(): String{
        if((razonSocial?:"").isEmpty())
            return "(Raz. Social incompleto)"
        return razonSocial!!
    }

    fun getNombreORazSocial(): String{
        return when(idTipoUsuario){
            TipoUsuario.DISTR.id -> getRazSocial()
            else -> getNombreCompleto()
        }
    }

    fun getRucRazSocial(): String {
        return "${getNroDoc()}\n${getRazSocial()}\n$correo"
    }

    fun getNroDoc(): String{
        if((nroDocumento?:"").isEmpty())
            return "(Doc. incompleto)"
        return nroDocumento!!
    }

    fun getNombreDoc(): String{
        return when(idTipoDocumento){
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