package pe.com.carwashperuapp.carwashapp.model

const val SERVICE_STATE_ATTENDED = 1
const val SERVICE_STATE_PENDING = 2
const val SERVICE_STATE_CANCELED = 3

class Servicio(
    var name: String,
    var schedule: String,
    var price: Float,
    var state: Int
) {
    companion object {
        val dataSet = arrayOf(
            Servicio("Lavado de Carro", "4:00 pm 4:30 pm", 30.00f, SERVICE_STATE_ATTENDED),
            Servicio("Pulido", "4:30 pm 5:00 pm", 77.00f, SERVICE_STATE_PENDING),
            Servicio("Limpieza Interior", "5:00 pm 5:30 pm", 50.00f, SERVICE_STATE_PENDING),
            Servicio("Lavado de Motor", "5:30 pm 6:00 pm", 90.00f, SERVICE_STATE_CANCELED)
        )
    }
}