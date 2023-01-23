package pe.com.carwashperuapp.carwashapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import pe.com.carwashperuapp.carwashapp.database.SesionData

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(
            SesionData(this),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }

        viewModel.apply {
            status.observe(this@SplashActivity) {
                when (it) {
                    Status.LOGIN -> goLogin()
                    Status.ADMIN -> goAdmin()
                    Status.CLIENT -> goClient()
                    Status.DISTRIB -> goDist()
                    else -> {}
                }
            }
            verificarSesion()
        }
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goAdmin() {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goClient() {
        val intentSrv = Intent(this, SincroCliService::class.java)
        startService(intentSrv)
        val intent = Intent(this, MainCliActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goDist() {
        val intent = Intent(this, MainDisActivity::class.java)
        startActivity(intent)
        finish()
    }

}