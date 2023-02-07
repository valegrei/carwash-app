package pe.com.carwashperuapp.carwashapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.ActivityMainDisBinding

class MainDisActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainDisBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(SesionData(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainDisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_content_main_dis)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_reserve_list,
                R.id.nav_my_places,
                R.id.nav_my_services,
                R.id.nav_my_schedules,
                R.id.navigation_menu,
            )
        )
        //Limpio subtitulos al cambiar de fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_account, R.id.navigation_account_edit, R.id.navigation_menu -> {
                    binding.collapsingToolbar.isTitleEnabled = true
                    binding.lyBanner.visibility = View.VISIBLE
                    binding.appbar.setExpanded(true, true)
                }
                else -> {
                    binding.collapsingToolbar.isTitleEnabled = false
                    binding.lyBanner.visibility = View.GONE
                    binding.appbar.setExpanded(false, false)
                    supportActionBar?.title = destination.label
                }
            }
            binding.toolbar.subtitle = null
        }

        //setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(
            binding.collapsingToolbar, binding.toolbar, navController, appBarConfiguration
        )
        navView.setupWithNavController(navController)

        viewModel.sesionStatus.observe(this) {
            when (it) {
                SesionStatus.CLOSED -> goLogin()
                else -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}