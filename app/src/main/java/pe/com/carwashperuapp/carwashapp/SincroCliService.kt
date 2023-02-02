package pe.com.carwashperuapp.carwashapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import pe.com.carwashperuapp.carwashapp.database.AppDataBase
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.network.Api

class SincroCliService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sincronizar()
        return START_STICKY
    }

    private fun sincronizar() {
        val sesionData = SesionData(this)
        val sesion = sesionData.getCurrentSesion()!!
        val dataBase = (application as CarwashApplication).database
        scope.async {
            sincroVehiculos(sesionData, sesion, dataBase)
            sincroDirecciones(sesionData, sesion, dataBase)
            sincroAnuncios(sesionData, sesion, dataBase)
        }
    }

    private suspend fun sincroVehiculos(
        sesionData: SesionData,
        sesion: Sesion,
        dataBase: AppDataBase
    ) {
        try {
            val lastSincroVehiculos = sesionData.getLastSincroVehiculos()
            val respVehiculos = Api.retrofitService.obtenerVehiculos(
                lastSincroVehiculos,
                sesion.getTokenBearer()
            )
            dataBase.vehiculoDao().guardarVehiculos(respVehiculos.data.vehiculos)
            sesionData.saveLastSincroVehiculos(respVehiculos.timeStamp)
        } catch (_: Exception) {
        }
    }

    private suspend fun sincroDirecciones(
        sesionData: SesionData,
        sesion: Sesion,
        dataBase: AppDataBase
    ) {
        try {
            val lastSincroDirecciones = sesionData.getLastSincroDirecciones()
            val respDirecciones = Api.retrofitService.obtenerDirecciones(
                lastSincroDirecciones,
                sesion.getTokenBearer()
            )
            dataBase.direccionDao().guardarDirecciones(respDirecciones.data.direcciones)
            sesionData.saveLastSincroDirecciones(respDirecciones.timeStamp)
        } catch (_: Exception) {
        }
    }

    private suspend fun sincroAnuncios(
        sesionData: SesionData,
        sesion: Sesion,
        dataBase: AppDataBase
    ) {
        try {
            val lastSincroAnuncios = sesionData.getLastSincroAnuncios()
            val respAnuncios = Api.retrofitService.obtenerAnunciosCli(
                lastSincroAnuncios,
                sesion.getTokenBearer()
            )
            dataBase.anuncioDao().guardarAnuncios(respAnuncios.data.anuncios)
            sesionData.saveLastSincroAnuncios(respAnuncios.timeStamp)
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}