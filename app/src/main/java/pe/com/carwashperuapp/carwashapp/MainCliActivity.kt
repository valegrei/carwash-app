package pe.com.carwashperuapp.carwashapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.ActivityMainCliBinding
import pe.com.carwashperuapp.carwashapp.network.BASE_URL
import pe.com.carwashperuapp.carwashapp.ui.announcement_cli.AnuncioDialog
import pe.com.carwashperuapp.carwashapp.ui.announcement_cli.AnunciosViewModel
import pe.com.carwashperuapp.carwashapp.ui.announcement_cli.AnunciosViewModelFactory

class MainCliActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainCliBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(SesionData(this))
    }
    private val anuncionsViewModel: AnunciosViewModel by viewModels {
        AnunciosViewModelFactory((application as CarwashApplication).database.anuncioDao())
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
                R.id.nav_favoritos,
                R.id.nav_my_cars,
                R.id.nav_my_places,
                R.id.navigation_menu,
            ), drawerLayout
        )
        //Limpio subtitulos al cambiar de fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.localFragment -> {
                    binding.appBarMainCli.lyBanner.visibility = View.VISIBLE
                    binding.appBarMainCli.collapsingToolbar.isTitleEnabled = true
                    binding.appBarMainCli.appbar.setExpanded(true, true)
                }
                else -> {
                    binding.appBarMainCli.fabReservar.visibility = View.GONE
                    binding.appBarMainCli.collapsingToolbar.isTitleEnabled = false
                    binding.appBarMainCli.lyBanner.visibility = View.GONE
                    binding.appBarMainCli.appbar.setExpanded(false, true)
                    supportActionBar?.title = destination.label
                }
            }
            binding.appBarMainCli.toolbar.subtitle = null
        }
        //setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(
            binding.appBarMainCli.collapsingToolbar,
            binding.appBarMainCli.toolbar,
            navController,
            appBarConfiguration
        )
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        mostrarFotoVehiculo()
        viewModel.sesionStatus.observe(this) {
            when (it) {
                SesionStatus.CLOSED -> goLogin()
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mostrarAnuncios()
    }

    private fun mostrarAnuncios() {
        if (anuncionsViewModel.mostrarAnuncios()) {
            Handler(Looper.getMainLooper()).postDelayed({
                anuncionsViewModel.setVisto()
                val dialog = AnuncioDialog()
                dialog.show(supportFragmentManager, "anuncioDialog")
            }, 2000)
        }
    }

    private fun mostrarFotoVehiculo() {
        val vehiculoDao = (application as CarwashApplication).database.vehiculoDao()
        lifecycleScope.launch {
            val sesion = viewModel.sesion.value!!
            vehiculoDao.obtenerPathFotoVehiculo(sesion.usuario.id!!).collect {
                val header = binding.navView.getHeaderView(0)
                val imgCar = header.findViewById<ImageView>(R.id.img_car)
                val tvEmail = header.findViewById<TextView>(R.id.tv_email)
                tvEmail.text = sesion.usuario.correo
                if (!it.isNullOrEmpty()) {
                    val url = "$BASE_URL$it"
                    imgCar.scaleType = ImageView.ScaleType.CENTER_CROP
                    imgCar.load(url) {
                        setHeader("Authorization", sesion.getTokenBearer())
                        transformations(CircleCropTransformation())
                        placeholder(R.drawable.loading_animation)
                        error(R.drawable.ic_broken_image)
                    }
                } else {
                    imgCar.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imgCar.load(R.drawable.logo)
                }
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