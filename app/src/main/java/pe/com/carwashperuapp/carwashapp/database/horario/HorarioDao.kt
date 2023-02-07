package pe.com.carwashperuapp.carwashapp.database.horario

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horario_config WHERE idDistrib = :idDistrib AND estado = 1")
    fun obtenerHorarioConfigs(idDistrib: Int): Flow<List<HorarioConfig>>

    @Transaction
    @Query("SELECT * FROM horario_config WHERE idDistrib = :idDistrib AND estado = 1")
    fun obtenerHorarioConfigLocales(idDistrib: Int): Flow<List<HorarioConfigLocal>>

    @RawQuery
    fun verificarInterseciones(query: SupportSQLiteQuery): Int
    @Transaction
    @Query("SELECT COUNT(*) FROM horario_config WHERE idLocal = :idLocal AND id != :idHorarioConfig AND estado = 1")
    fun verificarConflictos(idLocal: Int, idHorarioConfig: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarHorarioConfig(horarioConfigs: List<HorarioConfig>)

    @Delete
    suspend fun eliminarHorarioConfig(horarioConfig: HorarioConfig)
}