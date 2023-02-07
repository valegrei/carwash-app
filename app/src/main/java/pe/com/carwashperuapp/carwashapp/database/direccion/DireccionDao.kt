package pe.com.carwashperuapp.carwashapp.database.direccion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DireccionDao {
    @Query("SELECT * FROM direccion WHERE idUsuario = :idUsuario AND estado = 1")
    fun obtenerDirecciones(idUsuario: Int): Flow<List<Direccion>>

    @Query("SELECT * FROM direccion WHERE idUsuario = :idUsuario AND estado = 1 ")
    suspend fun obtenerDirecciones2(idUsuario: Int): List<Direccion>

    @Insert(onConflict = REPLACE)
    suspend fun guardarDirecciones(servicio: List<Direccion>)
}