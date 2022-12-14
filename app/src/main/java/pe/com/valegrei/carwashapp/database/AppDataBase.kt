package pe.com.valegrei.carwashapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.com.valegrei.carwashapp.database.anuncio.Anuncio
import pe.com.valegrei.carwashapp.database.anuncio.AnuncioDao
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.database.usuario.UsuarioDao

@Database(entities = [Usuario::class, Anuncio::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun anuncioDao(): AnuncioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDataBase::class.java,
                    "carwash_db"
                )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}