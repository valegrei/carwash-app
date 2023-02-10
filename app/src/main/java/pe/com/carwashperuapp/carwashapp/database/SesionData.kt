package pe.com.carwashperuapp.carwashapp.database

import android.content.Context
import android.content.SharedPreferences
import pe.com.carwashperuapp.carwashapp.database.sesion.Sesion
import pe.com.carwashperuapp.carwashapp.database.usuario.Usuario
import java.util.*

private const val PREF_NAME = "sesion_data"
private const val SESION_EXP_KEY = "sesion_exp"
private const val SESION_TOKEN_KEY = "sesion_token"
private const val SESION_ESTADO_KEY = "sesion_estado"
private const val USU_ID_KEY = "usu_id"
private const val USU_CORREO_KEY = "usu_correo"
private const val USU_NOMBRES_KEY = "usu_nombres"
private const val USU_APE_PATERNO_KEY = "usu_ape_paterno"
private const val USU_APE_MATERNO_KEY = "usu_ape_materno"
private const val USU_RAZ_SOC_KEY = "usu_razon_social"
private const val USU_NRO_DOC_KEY = "usu_nro_doc"
private const val USU_NRO_CEL1_KEY = "usu_nro_cel1"
private const val USU_NRO_CEL2_KEY = "usu_nro_cel2"
private const val USU_ESTADO_KEY = "usu_estado_act"
private const val USU_ID_TIPO_KEY = "usu_id_tipo"
private const val USU_ID_TIPO_DOC_KEY = "usu_id_tipo_doc"
private const val USU_ACERCA_DE = "usu_acerca_de"
private const val USU_BANNER_PATH = "usu_banner_path"
private const val SINCRO_USUARIOS = "sincro_usuarios"
private const val SINCRO_ANUNCIOS = "sincro_anuncios"
private const val SINCRO_PARAMETROS = "sincro_parametros"
private const val SINCRO_SERVICIOS = "sincro_servicios"
private const val SINCRO_DIRECCIONES = "sincro_direcciones"
private const val SINCRO_HORARIO_CONFIG = "sincro_horario_config"
private const val SINCRO_VEHICULOS = "sincro_vehiculos"
private const val DB_VERSION_NUM = "db_version_num"

class SesionData(context: Context) {
    private var pref: SharedPreferences

    init {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun closeSesion() {
        val editor = pref.edit()
        editor.putBoolean(SESION_ESTADO_KEY, false)
        editor.apply()
    }

    fun checkDBVersion() {
        if (pref.getInt(DB_VERSION_NUM, 0) != DB_VERSION) {
            //Nueva version de DB, se tuvo que limpiar, por lo tanto reiniciar sincronizacion
            val editor = pref.edit()
            editor.putInt(DB_VERSION_NUM, DB_VERSION)
            editor.putString(SINCRO_USUARIOS, null)
            editor.putString(SINCRO_ANUNCIOS, null)
            editor.putString(SINCRO_PARAMETROS, null)
            editor.putString(SINCRO_SERVICIOS, null)
            editor.putString(SINCRO_DIRECCIONES, null)
            editor.putString(SINCRO_HORARIO_CONFIG, null)
            editor.putString(SINCRO_VEHICULOS, null)
            editor.apply()
        }
    }

    fun saveSesion(sesion: Sesion) {
        val editor = pref.edit()
        //guardando datos sesion
        editor.putLong(SESION_EXP_KEY, sesion.fechaExpira.time)
        editor.putString(SESION_TOKEN_KEY, sesion.tokenAuth)
        editor.putBoolean(SESION_ESTADO_KEY, sesion.estado)
        //guardando datos de usuario
        editor.putInt(USU_ID_KEY, sesion.usuario.id!!)
        editor.putString(USU_CORREO_KEY, sesion.usuario.correo)
        editor.putString(USU_NOMBRES_KEY, sesion.usuario.nombres)
        editor.putString(USU_APE_PATERNO_KEY, sesion.usuario.apellidoPaterno)
        editor.putString(USU_APE_MATERNO_KEY, sesion.usuario.apellidoMaterno)
        editor.putString(USU_RAZ_SOC_KEY, sesion.usuario.razonSocial)
        editor.putString(USU_NRO_DOC_KEY, sesion.usuario.nroDocumento)
        editor.putString(USU_NRO_CEL1_KEY, sesion.usuario.nroCel1)
        editor.putString(USU_NRO_CEL2_KEY, sesion.usuario.nroCel2)
        editor.putInt(USU_ESTADO_KEY, sesion.usuario.estado)
        editor.putInt(USU_ID_TIPO_KEY, sesion.usuario.idTipoUsuario)
        editor.putInt(USU_ID_TIPO_DOC_KEY, sesion.usuario.idTipoDocumento)
        editor.putString(USU_ACERCA_DE, sesion.usuario.acercaDe)
        editor.putString(USU_BANNER_PATH, sesion.usuario.path)
        editor.apply()
    }

    fun getCurrentSesion(): Sesion? {
        val logueado = pref.getBoolean(SESION_ESTADO_KEY, false)
        if (!logueado)
            return null
        return Sesion(
            Date(pref.getLong(SESION_EXP_KEY, 0)),
            pref.getString(SESION_TOKEN_KEY, "")!!,
            Usuario(
                pref.getInt(USU_ID_KEY, 0),
                pref.getString(USU_CORREO_KEY, "")!!,
                "",
                pref.getString(USU_NOMBRES_KEY, ""),
                pref.getString(USU_APE_PATERNO_KEY, ""),
                pref.getString(USU_APE_MATERNO_KEY, ""),
                pref.getString(USU_RAZ_SOC_KEY, ""),
                pref.getString(USU_NRO_DOC_KEY, ""),
                pref.getString(USU_NRO_CEL1_KEY, ""),
                pref.getString(USU_NRO_CEL2_KEY, ""),
                pref.getInt(USU_ESTADO_KEY, 0),
                pref.getInt(USU_ID_TIPO_KEY, 0),
                pref.getInt(USU_ID_TIPO_DOC_KEY, 0),
                pref.getString(USU_ACERCA_DE, null),
                pref.getString(USU_BANNER_PATH, ""),
            ),
            true
        )
    }

    fun clearLastSincroUsuarios() {
        val edit = pref.edit()
        edit.putString(SINCRO_USUARIOS, null)
        edit.apply()
    }

    fun saveLastSincroUsuarios(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_USUARIOS, lastSincro)
        edit.apply()
    }

    fun getLastSincroUsuarios(): String? {
        return pref.getString(SINCRO_USUARIOS, null)
    }

    fun clearLastSincroAnuncios() {
        val edit = pref.edit()
        edit.putString(SINCRO_ANUNCIOS, null)
        edit.apply()
    }

    fun saveLastSincroAnuncios(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_ANUNCIOS, lastSincro)
        edit.apply()
    }

    fun getLastSincroAnuncios(): String? {
        return pref.getString(SINCRO_ANUNCIOS, null)
    }

    fun clearLastSincroParametros() {
        val edit = pref.edit()
        edit.putString(SINCRO_PARAMETROS, null)
        edit.apply()
    }

    fun saveLastSincroParametros(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_PARAMETROS, lastSincro)
        edit.apply()
    }

    fun getLastSincroParametros(): String? {
        return pref.getString(SINCRO_PARAMETROS, null)
    }


    fun clearLastSincroServicios() {
        val edit = pref.edit()
        edit.putString(SINCRO_SERVICIOS, null)
        edit.apply()
    }

    fun saveLastSincroServicios(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_SERVICIOS, lastSincro)
        edit.apply()
    }

    fun getLastSincroServicios(): String? {
        return pref.getString(SINCRO_SERVICIOS, null)
    }

    fun clearLastSincroDirecciones() {
        val edit = pref.edit()
        edit.putString(SINCRO_DIRECCIONES, null)
        edit.apply()
    }

    fun saveLastSincroDirecciones(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_DIRECCIONES, lastSincro)
        edit.apply()
    }

    fun getLastSincroDirecciones(): String? {
        return pref.getString(SINCRO_DIRECCIONES, null)
    }

    fun clearLastSincroHorarioConfigs() {
        val edit = pref.edit()
        edit.putString(SINCRO_HORARIO_CONFIG, null)
        edit.apply()
    }

    fun saveLastSincroHorarioConfigs(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_HORARIO_CONFIG, lastSincro)
        edit.apply()
    }

    fun getLastSincroHorarioConfigs(): String? {
        return pref.getString(SINCRO_HORARIO_CONFIG, null)
    }

    fun clearLastSincroVehiculos() {
        val edit = pref.edit()
        edit.putString(SINCRO_VEHICULOS, null)
        edit.apply()
    }

    fun saveLastSincroVehiculos(lastSincro: String?) {
        val edit = pref.edit()
        edit.putString(SINCRO_VEHICULOS, lastSincro)
        edit.apply()
    }

    fun getLastSincroVehiculos(): String? {
        return pref.getString(SINCRO_VEHICULOS, null)
    }
}