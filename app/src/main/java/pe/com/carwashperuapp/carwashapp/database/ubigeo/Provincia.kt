package pe.com.carwashperuapp.carwashapp.database.ubigeo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "provincia", foreignKeys = [ForeignKey(
        entity = Departamento::class,
        parentColumns = arrayOf("id_departamento"),
        childColumns = arrayOf("id_departamento"),
        onDelete = ForeignKey.SET_NULL,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Provincia(
    @PrimaryKey
    @ColumnInfo(name = "id_provincia")
    val idProvincia: Int,
    @ColumnInfo(name = "provincia")
    val provincia: String,
    @ColumnInfo(name = "id_departamento", index = true)
    val idDepartamento: Int,
){
    override fun toString(): String {
        return provincia
    }
}