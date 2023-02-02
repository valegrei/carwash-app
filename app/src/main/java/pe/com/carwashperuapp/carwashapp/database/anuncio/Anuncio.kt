package pe.com.carwashperuapp.carwashapp.database.anuncio

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.network.BASE_URL

@JsonClass(generateAdapter = true)
@Entity(tableName = "anuncios")
data class Anuncio(
    @PrimaryKey
    @Json(name = "id")
    @ColumnInfo(name = "id")
    var id: Int,
    @Json(name = "descripcion")
    @ColumnInfo(name = "descripcion")
    var descripcion: String?,
    @Json(name = "url")
    @ColumnInfo(name = "url")
    var url: String?,
    @Json(name = "path")
    @ColumnInfo(name = "path")
    var path: String,
    @Json(name = "mostrar")
    @ColumnInfo(name = "mostrar")
    var mostrar: Boolean,
    @Json(name = "estado")
    @ColumnInfo(name = "estado")
    var estado: Boolean,
    @Ignore
    var selected: Boolean?,
) {
    constructor() : this(
        0, null, null, "", false,false, null,
    )

    fun getUrlArchivo(): String {
        return "$BASE_URL$path"
    }
}