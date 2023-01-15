package pe.com.carwashperuapp.carwashapp.database.ubigeo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departamento")
data class Departamento(
    @PrimaryKey
    @ColumnInfo(name = "id_departamento")
    val idDepartamento: Int,
    @ColumnInfo(name = "departamento")
    val departamento: String,
){
    override fun toString(): String {
        return departamento
    }
}