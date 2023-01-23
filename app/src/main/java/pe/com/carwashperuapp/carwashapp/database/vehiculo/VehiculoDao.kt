package pe.com.carwashperuapp.carwashapp.database.vehiculo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Query("SELECT * FROM vehiculos WHERE id_cliente = :idCliente AND estado = 1")
    fun obtenerVehiculos(idCliente: Int): Flow<List<Vehiculo>>

    @Query("SELECT * FROM vehiculos WHERE id_cliente = :idCliente AND estado = 1")
    suspend fun obtenerVehiculos2(idCliente: Int): List<Vehiculo>

    @Insert(onConflict = REPLACE)
    suspend fun guardarVehiculos(servicio: List<Vehiculo>)
}