package pe.com.valegrei.carwashapp.model

class AccountData(
    var razonSocial: String,
    var ruc: String,
    var telefono: String,
    var whatsapp: String,
    var email: String
) {
    companion object {
        val cuenta = AccountData(
            "Lavado de Carros Santa Rosa",
            "10709582864",
            "925416855",
            "958412963",
            "example@gmail.com"
        )
    }
}