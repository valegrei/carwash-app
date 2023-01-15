package pe.com.carwashperuapp.carwashapp

import android.app.Application
import pe.com.carwashperuapp.carwashapp.database.AppDataBase

class CarwashApplication: Application() {
    val database: AppDataBase by lazy {
        AppDataBase.getDatabase(this)
    }
}