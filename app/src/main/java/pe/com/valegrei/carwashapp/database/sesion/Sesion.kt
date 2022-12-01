package pe.com.valegrei.carwashapp.database.sesion

import pe.com.valegrei.carwashapp.database.usuario.Usuario

data class Sesion(
    var fechaExpira: String,
    var tokenAuth: String,
    var usuario: Usuario,
    var estado: Boolean,
)