package pe.com.valegrei.carwashapp

import android.app.Application
import pe.com.valegrei.carwashapp.database.AppDataBase

class CarwashApplication: Application() {
    val database: AppDataBase by lazy {
        AppDataBase.getDatabase(this)
    }
}