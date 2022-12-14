package pe.com.valegrei.carwashapp.database.anuncio

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import pe.com.valegrei.carwashapp.network.BASE_URL

@Entity(tableName = "anuncios")
data class Anuncio(
    @PrimaryKey
    @Json(name = "id")
    var id: Int,
    @Json(name = "descripcion")
    var descripcion: String?,
    @Json(name = "url")
    var url: String?,
    @Json(name = "path")
    var path: String,
    @Json(name = "estado")
    var estado: Boolean,
    @Ignore
    var selected: Boolean?,
) {
    constructor() : this(
        0, null, null, "", false, null,
    )

    fun getUrlArchivo(): String {
        return "$BASE_URL$path"
    }
}