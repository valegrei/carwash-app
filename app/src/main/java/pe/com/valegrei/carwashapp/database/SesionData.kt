package pe.com.valegrei.carwashapp.database

import android.content.Context
import android.content.SharedPreferences
import pe.com.valegrei.carwashapp.database.sesion.Sesion
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.model.Archivo
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
private const val USU_NOMBRE_ARCHIVO = "usu_nombre_archivo"
private const val USU_DIST_ACT_KEY = "usu_dist_act"
private const val USU_VERIFICADO_KEY = "usu_verificado"
private const val USU_ESTADO_KEY = "usu_estado_act"
private const val USU_ID_TIPO_KEY = "usu_id_tipo"
private const val USU_ID_TIPO_DOC_KEY = "usu_id_tipo_doc"
private const val USU_CREATED_AT_KEY = "usu_created_at"
private const val USU_UPDATED_AT_KEY = "usu_updated_at"
private const val SINCRO_USUARIOS = "sincro_usuarios"

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
        editor.putBoolean(USU_DIST_ACT_KEY, sesion.usuario.distAct)
        editor.putBoolean(USU_VERIFICADO_KEY, sesion.usuario.verificado)
        editor.putBoolean(USU_ESTADO_KEY, sesion.usuario.estado)
        editor.putInt(USU_ID_TIPO_KEY, sesion.usuario.idTipoUsuario)
        editor.putInt(USU_ID_TIPO_DOC_KEY, sesion.usuario.idTipoDocumento)
        editor.putLong(USU_CREATED_AT_KEY, sesion.usuario.createdAt?.time!!)
        editor.putLong(USU_UPDATED_AT_KEY, sesion.usuario.updatedAt?.time!!)
        if(sesion.usuario.archivo != null)
            editor.putString(USU_NOMBRE_ARCHIVO, sesion.usuario.archivo?.nombre!!)
        else
            editor.putString(USU_NOMBRE_ARCHIVO, null)
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
                pref.getBoolean(USU_DIST_ACT_KEY, false),
                pref.getBoolean(USU_VERIFICADO_KEY, false),
                pref.getBoolean(USU_ESTADO_KEY, false),
                pref.getInt(USU_ID_TIPO_KEY, 0),
                pref.getInt(USU_ID_TIPO_DOC_KEY, 0),
                Date(pref.getLong(USU_CREATED_AT_KEY, 0)),
                Date(pref.getLong(USU_UPDATED_AT_KEY, 0)),
                Archivo(pref.getString(USU_NOMBRE_ARCHIVO, null)),
            ),
            true
        )
    }

    fun saveLastSincroUsuarios(lastSincro: Date) {
        val edit = pref.edit()
        edit.putLong(SINCRO_USUARIOS, lastSincro.time)
        edit.apply()
    }

    fun getLastSincroUsuarios(): Date {
        return Date(pref.getLong(SINCRO_USUARIOS, 0))
    }
}