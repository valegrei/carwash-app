package pe.com.valegrei.carwashapp

import android.app.Application
import pe.com.valegrei.carwashapp.database.AppDatabase

class CarwashApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}