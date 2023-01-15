package pe.com.carwashperuapp.carwashapp.model

class Reserva(
    var client: String,
    var vehicle: String,
    var place: String,
    var hora: String,
) {
    companion object {
        val dataSet = arrayOf(
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","8:00"),
            Reserva("Brandon Tejada Luna", "Toyota Corola, Negro", "Calle los tulipanes 1956","10:00"),
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","11:00"),
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","14:30"),
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","15:00")
        )
    }
}