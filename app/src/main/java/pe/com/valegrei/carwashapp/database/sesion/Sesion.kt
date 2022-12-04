package pe.com.valegrei.carwashapp.database.sesion

import pe.com.valegrei.carwashapp.database.usuario.Usuario
import java.util.*

data class Sesion(
    var fechaExpira: Date,
    var tokenAuth: String,
    var usuario: Usuario,
    var estado: Boolean,
){
    fun getTokenBearer() = "Bearer $tokenAuth"
}