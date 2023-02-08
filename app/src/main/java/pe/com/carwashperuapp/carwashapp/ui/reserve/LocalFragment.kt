package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

class LocalFragment : Fragment() {

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

        viewModel.apply {
            goStatus.observe(viewLifecycleOwner) {
                if (it == GoStatus.GO_CONFIRM) goConfirmarReserva()
            }
            selectedLocal.observe(viewLifecycleOwner) {
                setTitle(it.distrib?.razonSocial)
                bindImageBanner(
                    requireActivity().findViewById(R.id.img_banner),
                    it.distrib?.getURLFoto()
                )
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
        viewModel.reservar()
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
        Log.d("ESTADO","pausa")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ESTADO","resumen")
    }
}