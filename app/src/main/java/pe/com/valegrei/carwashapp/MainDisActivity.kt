package pe.com.valegrei.carwashapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.ActivityMainDisBinding

class MainDisActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainDisBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(SesionData(this))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainDisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainDis.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main_dis)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_reserve_list,
                R.id.nav_my_data,
                R.id.nav_my_places,
                R.id.nav_my_services,
                R.id.nav_my_schedules
            ), drawerLayout
        )

        //Limpio subtitulos al cambiar de fragment
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.appBarMainDis.toolbar.subtitle = null
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        viewModel.sesionStatus.observe(this){
            when(it){
                SesionStatus.CLOSED -> goLogin()
                else -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_dis)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun goLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}