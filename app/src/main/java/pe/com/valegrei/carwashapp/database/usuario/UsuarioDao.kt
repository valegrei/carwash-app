package pe.com.valegrei.carwashapp.database.usuario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario")
    fun obtenerUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE id_tipo_usuario = :tipoDist AND estado = 1")
    fun obtenerDistribuidores(tipoDist: Int = TipoUsuario.DISTR.id): Flow<List<Usuario>>

    @Insert(onConflict = REPLACE)
    suspend fun guardarUsuarios(usuarios: List<Usuario>)

    @Insert(onConflict = REPLACE)
    suspend fun guardarUsuario(usuario: Usuario)
}