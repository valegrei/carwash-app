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

    @Query("SELECT path FROM vehiculos where id_cliente = :idCliente AND path IS NOT NULL AND estado=1 LIMIT 1")
    fun obtenerPathFotoVehiculo(idCliente: Int): Flow<String?>

    @Insert(onConflict = REPLACE)
    suspend fun guardarVehiculos(servicio: List<Vehiculo>)
    @Query("DELETE FROM vehiculos")
    fun limpiar()
}