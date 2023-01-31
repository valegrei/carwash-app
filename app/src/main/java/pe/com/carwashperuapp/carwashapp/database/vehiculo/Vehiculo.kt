package pe.com.carwashperuapp.carwashapp.database.vehiculo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.network.BASE_URL

@Entity(tableName = "vehiculos")
data class Vehiculo(
    @PrimaryKey @Json(name = "id") @ColumnInfo(name = "id") val id: Int,
    @Json(name = "marca") @ColumnInfo(name = "marca") val marca: String,
    @Json(name = "modelo") @ColumnInfo(name = "modelo") val modelo: String,
    @Json(name = "year") @ColumnInfo(name = "year") val year: Int,
    @Json(name = "placa") @ColumnInfo(name = "placa") val placa: String,
    @Json(name = "path") @ColumnInfo(name = "path") val path: String?,
    @Json(name = "estado") @ColumnInfo(name = "estado") val estado: Boolean?,
    @Json(name = "idCliente") @ColumnInfo(name = "id_cliente") val idCliente: Int?,
){
    override fun toString(): String = "$marca $modelo $year\n$placa"
    fun nombreLinea(): String = "$marca $modelo $year - $placa"
    fun getUrlArchivo(): String? = if(path.isNullOrEmpty()) null else "$BASE_URL$path"
    fun getYearStr(): String = year.toString()
}