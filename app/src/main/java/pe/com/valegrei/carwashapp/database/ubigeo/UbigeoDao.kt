package pe.com.valegrei.carwashapp.database.ubigeo

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UbigeoDao {
    @Query("SELECT * FROM departamento ORDER BY departamento ASC")
    suspend fun obtenerDepartamentos(): List<Departamento>

    @Query("SELECT * FROM provincia WHERE id_departamento = :idDepartamento ORDER BY provincia ASC")
    suspend fun obtenerProvincia(idDepartamento: Int): List<Provincia>

    @Query("SELECT * FROM distrito WHERE id_provincia = :idProvincia ORDER BY distrito ASC")
    suspend fun obtenerDistrito(idProvincia: Int): List<Distrito>
}