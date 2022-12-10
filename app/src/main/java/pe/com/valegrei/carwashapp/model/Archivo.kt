package pe.com.valegrei.carwashapp.model

import com.squareup.moshi.Json

data class Archivo(@Json(name = "path") var path: String?)