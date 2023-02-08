package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null

    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearLocales()
        //Adapter de RV
        val adapter = FavoritesListAdapter {
            viewModel.selectLocal(it)
            viewModel.completarOReservar()
        }

        binding.rvFavoriteList.adapter = adapter

        binding.swipeFavorites.setColorSchemeResources(R.color.purple)
        binding.swipeFavorites.setOnRefreshListener {
            viewModel.obtenerFavoritos()
        }
        viewModel.apply {
            locales.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> binding.swipeFavorites.isRefreshing = true
                    else -> binding.swipeFavorites.isRefreshing = false
                }
            }
            goStatus.observe(viewLifecycleOwner) {
                when (it) {
                    GoStatus.GO_ADD -> goNuevaReserva()
                    GoStatus.SHOW_COMPLETAR -> mostrarCompletarDatos()
                    else -> {}
                }
            }
        }
        viewModel.obtenerFavoritos()
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

    private fun goNuevaReserva() {
        findNavController().navigate(R.id.action_nav_favoritos_to_localFragment)
        viewModel.clearGoStatus()
    }

    private fun goDatos() {
        findNavController().navigate(R.id.action_nav_favoritos_to_navigation_account)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}