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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarHorarioConfig(horarioConfigs: List<HorarioConfig>)
}