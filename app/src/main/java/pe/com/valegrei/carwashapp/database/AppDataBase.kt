package pe.com.valegrei.carwashapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.com.valegrei.carwashapp.database.anuncio.Anuncio
import pe.com.valegrei.carwashapp.database.anuncio.AnuncioDao
import pe.com.valegrei.carwashapp.database.parametro.Parametro
import pe.com.valegrei.carwashapp.database.parametro.ParametroDao
import pe.com.valegrei.carwashapp.database.servicio.Servicio
import pe.com.valegrei.carwashapp.database.servicio.ServicioDao
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao

const val DB_VERSION = 5

@Database(
    entities = [Usuario::class, Anuncio::class, Parametro::class, Servicio::class],
    version = DB_VERSION
)
@TypeConverters(DateConverters::class, BigDecimalConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun anuncioDao(): AnuncioDao
    abstract fun parametroDao(): ParametroDao
    abstract fun servicioDao(): ServicioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDataBase::class.java,
                    "carwash_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}