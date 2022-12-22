package pe.com.valegrei.carwashapp.model

class Local(
    var departamento: String,
    var provincia: String,
    var distrito: String,
    var direccion: String,
    var lat: Float,
    var long: Float
) {
    fun getUbigeoDireccion(): String{
        return "$departamento - $provincia - $departamento\n$direccion"
    }

    companion object {
        val dataSet = arrayOf(
            Local("Lima", "Lima", "","Calle los tulipanes 1956",0f,0f),
            Local("Lima", "Lima", "","Calle los tulipanes 1956",0f,0f),
            Local("Lima", "Lima", "","Calle los tulipanes 1956",0f,0f),
            Local("Lima", "Lima", "","Calle los tulipanes 1956",0f,0f),
            Local("Lima", "Lima", "","Calle los tulipanes 1956",0f,0f)
        )
    }
}