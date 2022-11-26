package pe.com.valegrei.carwashapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import pe.com.valegrei.carwashapp.databinding.ActivityMainDisBinding

class MainDisActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainDisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainDisBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_dis)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}