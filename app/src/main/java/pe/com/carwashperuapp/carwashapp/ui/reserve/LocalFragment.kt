package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentLocalReservaBinding
import pe.com.carwashperuapp.carwashapp.ui.bindImageBanner


class LocalFragment : Fragment(), MenuProvider {

    private var _binding: FragmentLocalReservaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = this@LocalFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.apply {
            goStatus.observe(viewLifecycleOwner) {
                when (it) {
                    GoStatus.SHOW_COMPLETAR -> mostrarCompletarDatos()
                    GoStatus.GO_CONFIRM -> goConfirmarReserva()
                    else -> {}
                }
            }
            selectedLocal.observe(viewLifecycleOwner) {
                setTitle(it.distrib?.razonSocial)
                bindImageBanner(
                    requireActivity().findViewById(R.id.img_banner),
                    it.distrib?.getURLFoto()
                )
            }
            mostrarFavorito.observe(viewLifecycleOwner) {
                mostrarFav(it)
            }
        }

        binding.viewPager.adapter = ScreenSlidePagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Reserva"
                1 -> tab.text = "Acerca de"
            }
        }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    mostrarBotonReserva()
                } else {
                    ocultarBotonReserva()
                }
            }
        })
    }

    fun reservar() {
        viewModel.seleccionadosServicios()
        viewModel.completarOReservar()
    }

    fun mostrarBotonReserva() {
        val btnReserva =
            requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab_reservar)
        btnReserva.visibility = View.VISIBLE
        btnReserva.setOnClickListener {
            reservar()
        }
    }

    fun ocultarBotonReserva() {
        val btnReserva =
            requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab_reservar)
        btnReserva.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ScreenSlidePagerAdapter(fr: Fragment) : FragmentStateAdapter(fr) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> ReserveFragment()
            else -> LocalDetalleFragment()
        }
    }

    private fun goConfirmarReserva() {
        viewModel.clearGoStatus()
        findNavController().navigate(R.id.action_localFragment_to_reserveResumenFragment)
    }

    private fun setTitle(title: String?) {
        val toolbar =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        toolbar.title = title
    }

    override fun onPause() {
        super.onPause()
        viewModel.guardarFavorito()
    }

    private var menuAddFav: MenuItem? = null
    private var menuDelFav: MenuItem? = null
    private var menuCall: MenuItem? = null
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.local_menu, menu)
        menuAddFav = menu.findItem(R.id.action_add_fav)
        menuDelFav = menu.findItem(R.id.action_del_fav)
        menuCall = menu.findItem(R.id.action_call)
        mostrarFav(viewModel.mostrarFavorito.value!!)
        mostrarLlamar()
    }

    private fun mostrarFav(mostrar: Boolean) {
        if (mostrar) {
            menuDelFav?.isVisible = true
            menuAddFav?.isVisible = false
        } else {
            menuDelFav?.isVisible = false
            menuAddFav?.isVisible = true
        }
    }

    private fun mostrarLlamar() {
        menuCall?.isVisible = viewModel.mostrarLlamar()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_add_fav -> {
                viewModel.marcarFavorito()
                true
            }
            R.id.action_del_fav -> {
                viewModel.desmarcarFavorito()
                true
            }
            R.id.action_navigation -> {
                mostrarRuta()
                true
            }
            R.id.action_call -> {
                llamar()
                true
            }
            else -> false
        }
    }

    private fun mostrarRuta() {
        val latitude = viewModel.selectedLocal.value?.latitud
        val longitude = viewModel.selectedLocal.value?.longitud
        val url = "waze://?ll=$latitude, $longitude&navigate=yes"
        var intentWaze = Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.waze")

        val uriGoogle = "google.navigation:q=$latitude,$longitude"
        var intentGoogleNav = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uriGoogle)
        ).setPackage("com.google.android.apps.maps")

        val title: String = "Seleccione"
        val chooserIntent = Intent.createChooser(intentGoogleNav, title)
        val arr = arrayOfNulls<Intent>(1)
        arr[0] = intentWaze
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
        startActivity(chooserIntent)
    }

    private fun llamar() {
        if (viewModel.mostrarLlamar()) {
            val nroCel = viewModel.selectedLocal.value?.distrib?.nroCel1
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$nroCel")
            startActivity(intent)
        }
    }

    private fun mostrarCompletarDatos() {
        viewModel.clearGoStatus()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.reserve_completar)
            .setMessage(R.string.reserve_completar_msg)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ ->
                goDatos()
            }.show()
    }

    private fun goDatos() {
        findNavController().navigate(R.id.action_localFragment_to_navigation_account)
    }
}