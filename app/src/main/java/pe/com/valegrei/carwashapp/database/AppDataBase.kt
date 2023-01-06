package pe.com.valegrei.carwashapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.com.valegrei.carwashapp.database.anuncio.Anuncio
import pe.com.valegrei.carwashapp.database.anuncio.AnuncioDao
import pe.com.valegrei.carwashapp.database.direccion.Direccion
import pe.com.valegrei.carwashapp.database.direccion.DireccionDao
import pe.com.valegrei.carwashapp.database.parametro.Parametro
import pe.com.valegrei.carwashapp.database.parametro.ParametroDao
import pe.com.valegrei.carwashapp.database.servicio.Servicio
import pe.com.valegrei.carwashapp.database.servicio.ServicioDao
import pe.com.valegrei.carwashapp.database.ubigeo.Departamento
import pe.com.valegrei.carwashapp.database.ubigeo.Distrito
import pe.com.valegrei.carwashapp.database.ubigeo.Provincia
import pe.com.valegrei.carwashapp.database.ubigeo.UbigeoDao
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao

const val DB_VERSION = 6

@Database(
    entities = [
        Usuario::class,
        Anuncio::class,
        Parametro::class,
        Servicio::class,
        Departamento::class,
        Provincia::class,
        Distrito::class,
        Direccion::class,
    ],
    version = DB_VERSION
)
@TypeConverters(DateConverters::class, BigDecimalConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun anuncioDao(): AnuncioDao
    abstract fun parametroDao(): ParametroDao
    abstract fun servicioDao(): ServicioDao
    abstract fun ubigeoDao(): UbigeoDao
    abstract fun direccionDao(): DireccionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDataBase::class.java,
                    "carwash_db"
                )//.fallbackToDestructiveMigration()
                    .createFromAsset("database/ubigeos.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}