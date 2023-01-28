package pe.com.carwashperuapp.carwashapp.model

class Reserva1(
    var client: String,
    var vehicle: String,
    var place: String,
    var hora: String,
) {
    companion object {
        val dataSet = arrayOf(
            Reserva1("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","8:00"),
            Reserva1("Brandon Tejada Luna", "Toyota Corola, Negro", "Calle los tulipanes 1956","10:00"),
            Reserva1("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","11:00"),
            Reserva1("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","14:30"),
            Reserva1("Mario Trujillo Guzman", "Toyota Corola, Rojo", "Calle los tulipanes 1956","15:00")
        )
    }
}