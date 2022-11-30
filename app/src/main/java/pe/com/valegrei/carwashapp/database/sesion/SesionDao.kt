package pe.com.valegrei.carwashapp.database.sesion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SesionDao {
    @Transaction
    @Query("SELECT * FROM sesion WHERE estado=1")
    fun getSesionUsuario(): SesionUsuario

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSesion(sesion: Sesion)
}