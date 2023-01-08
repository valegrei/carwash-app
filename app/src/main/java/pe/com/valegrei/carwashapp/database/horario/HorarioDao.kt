package pe.com.valegrei.carwashapp.database.horario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horario_config WHERE idDistrib = :idDistrib AND estado = 1")
    fun obtenerHorarioConfigs(idDistrib: Int): Flow<List<HorarioConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarHorarioConfig(horarioConfigs: List<HorarioConfig>)
}