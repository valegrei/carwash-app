package pe.com.valegrei.carwashapp.model

class Reserva(
    var client: String,
    var vehicle: String,
    var place: String
) {
    companion object {
        val dataSet = arrayOf(
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956"),
            Reserva("Brandon Tejada Luna", "Toyota Corola, Negro", "Calle los tulipanes 1956"),
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956"),
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956"),
            Reserva("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956")
        )
    }
}