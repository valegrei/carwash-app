package pe.com.valegrei.carwashapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import pe.com.valegrei.carwashapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_login) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        layout.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

}