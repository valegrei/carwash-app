package pe.com.valegrei.carwashapp.database.ubigeo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "distrito", foreignKeys = [ForeignKey(
        entity = Provincia::class,
        parentColumns = arrayOf("id_provincia"),
        childColumns = arrayOf("id_provincia"),
        onDelete = ForeignKey.SET_NULL,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Distrito(
    @PrimaryKey
    @ColumnInfo(name = "id_distrito")
    val idDistrito: Int,
    @ColumnInfo(name = "distrito")
    val distrito: String,
    @ColumnInfo(name = "id_provincia")
    val idProvincia: Int,
    @ColumnInfo(name = "codigo")
    val codigo: String,
){
    override fun toString(): String {
        return distrito
    }
}