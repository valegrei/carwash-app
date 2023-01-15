package pe.com.carwashperuapp.carwashapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.com.carwashperuapp.carwashapp.database.anuncio.Anuncio
import pe.com.carwashperuapp.carwashapp.database.anuncio.AnuncioDao
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.database.direccion.DireccionDao
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioConfig
import pe.com.carwashperuapp.carwashapp.database.horario.HorarioDao
import pe.com.carwashperuapp.carwashapp.database.parametro.Parametro
import pe.com.carwashperuapp.carwashapp.database.parametro.ParametroDao
import pe.com.carwashperuapp.carwashapp.database.servicio.Servicio
import pe.com.carwashperuapp.carwashapp.database.servicio.ServicioDao
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Departamento
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Distrito
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Provincia
import pe.com.carwashperuapp.carwashapp.database.ubigeo.UbigeoDao
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import pe.com.carwashperuapp.carwashapp.database.usuario.UsuarioDao

const val DB_VERSION = 7

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
        HorarioConfig::class,
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
    abstract fun horarioDao(): HorarioDao

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
                    .allowMainThreadQueries()
                    .createFromAsset("database/ubigeos.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}