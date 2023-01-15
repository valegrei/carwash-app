package pe.com.carwashperuapp.carwashapp.database.parametro

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

const val PARAM_SERVER = 1
const val PARAM_CLIENT = 2

const val EMAIL_HOST = "EMAIL_HOST"
const val EMAIL_PORT = "EMAIL_PORT"
const val EMAIL_SSL_TLS = "EMAIL_SSL_TLS"
const val EMAIL_ADDR = "EMAIL_ADDR"
const val EMAIL_PASS = "EMAIL_PASS"

@Entity(tableName = "parametro")
data class Parametro(
    @Json(name = "clave")
    @PrimaryKey
    @ColumnInfo(name = "clave")
    var clave: String,
    @Json(name = "valor")
    @ColumnInfo(name = "valor")
    var valor: String,
    @Json(name = "idTipo")
    @ColumnInfo(name = "idTipo")
    var idTipo: Int,
)