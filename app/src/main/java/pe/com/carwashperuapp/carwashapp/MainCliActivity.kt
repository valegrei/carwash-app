package pe.com.carwashperuapp.carwashapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.ActivityMainCliBinding

class MainCliActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainCliBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(SesionData(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainCliBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainCli.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main_cli)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_reserve,
                R.id.nav_my_reserves,
                R.id.nav_my_cars,
                R.id.nav_my_places,
                R.id.navigation_menu,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        viewModel.sesionStatus.observe(this) {
            when (it) {
                SesionStatus.CLOSED -> goLogin()
                else -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_cli)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_cli)
        if (item.itemId == R.id.menu_logout) {
            mostrarCerrarSesion()
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true
        }
        //This is for maintaining the behavior of the Navigation view
        NavigationUI.onNavDestinationSelected(item, navController);
        //This is for closing the drawer after acting on it
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private fun mostrarCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle(R.string.menu_logout)
            .setMessage(R.string.logout_msg)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.cerrarSesion()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}