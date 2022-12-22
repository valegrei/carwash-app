package pe.com.valegrei.carwashapp.database.servicio

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Query("SELECT * FROM servicios WHERE idDistrib = :idDistribuidor AND estado = 1")
    fun obtenerServicios(idDistribuidor: Int): Flow<List<Servicio>>

    @Insert(onConflict = REPLACE)
    suspend fun guardarServicios(servicio: List<Servicio>)

}