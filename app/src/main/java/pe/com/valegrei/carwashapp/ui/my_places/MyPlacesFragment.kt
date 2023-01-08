package pe.com.valegrei.carwashapp.ui.my_places

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.direccion.Direccion
import pe.com.valegrei.carwashapp.databinding.FragmentMyPlacesBinding

class MyPlacesFragment : Fragment(), MenuProvider {

    private var _binding: FragmentMyPlacesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MyPlacesViewModel by activityViewModels {
        MyPlacesViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.ubigeoDao(),
        )
    }

    private lateinit var adapter: MyPlacesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Adapter de RV
        adapter = MyPlacesListAdapter { goLocal(it) }

        binding.rvPlacesList.adapter = adapter
        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarDirecciones().collect {
                adapter.submitList(it)
            }
        }

        binding.swipeLocales.setColorSchemeResources(R.color.purple)
        binding.swipeLocales.setOnRefreshListener {
            viewModel.descargarDirecciones()
        }
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeLocales.isRefreshing = true
                else -> binding.swipeLocales.isRefreshing = false
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.descargarDirecciones()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_add) {
            viewModel.nuevoDireccion()
            findNavController().navigate(R.id.action_nav_my_places_to_nav_place_detail)
            return true
        }
        return false
    }

    private fun goLocal(item: Direccion) {
        viewModel.verDireccion(item)
        findNavController().navigate(R.id.action_nav_my_places_to_nav_place_detail)
    }
}