package pe.com.carwashperuapp.carwashapp.ui.my_cars

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
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.databinding.FragmentMyCarsBinding

class MyCarsFragment : Fragment(), MenuProvider {

    private var _binding: FragmentMyCarsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MyCarsViewModel by activityViewModels {
        MyCarsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    private lateinit var adapter: MyCarsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Adapter de RV
        adapter = MyCarsListAdapter { goVehiculo(it) }

        binding.rvCarsList.adapter = adapter
        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarVehiculos().collect {
                adapter.submitList(it)
            }
        }

        binding.swipeCars.setColorSchemeResources(R.color.purple)
        binding.swipeCars.setOnRefreshListener {
            viewModel.descargarVehiculos()
        }
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeCars.isRefreshing = true
                else -> binding.swipeCars.isRefreshing = false
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.descargarVehiculos()
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
            viewModel.nuevoVehiculo()
            findNavController().navigate(R.id.action_nav_my_cars_to_nav_car_detail)
            return true
        }
        return false
    }

    private fun goVehiculo(item: Vehiculo) {
        viewModel.verVehiculo(item)
        findNavController().navigate(R.id.action_nav_my_cars_to_nav_car_detail)
    }
}