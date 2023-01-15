package pe.com.carwashperuapp.carwashapp.database.parametro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ParametroDao {
    @MapInfo(keyColumn = "clave", valueColumn = "valor")
    @Query("SELECT clave, valor FROM parametro WHERE idTipo = :idTipo")
    fun obtenerParametrosServer(idTipo: Int = PARAM_SERVER): Flow<Map<String, String>>

    @Insert(onConflict = REPLACE)
    suspend fun guardarParametros(parametros: List<Parametro>)

}