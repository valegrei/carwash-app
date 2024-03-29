package pe.com.carwashperuapp.carwashapp.database.anuncio

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnuncioDao {
    @Query("SELECT * FROM anuncios WHERE estado = 1")
    fun obtenerAnuncios(): Flow<List<Anuncio>>

    @Query("SELECT * FROM anuncios WHERE estado = 1 AND mostrar = 1")
    fun obtenerAnunciosMostrar(): List<Anuncio>

    @Insert(onConflict = REPLACE)
    suspend fun guardarAnuncios(usuarios: List<Anuncio>)

    @Query("DELETE FROM anuncios")
    fun limpiar()

}