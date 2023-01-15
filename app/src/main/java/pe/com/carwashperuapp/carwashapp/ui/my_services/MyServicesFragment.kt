package pe.com.carwashperuapp.carwashapp.ui.my_services

import android.app.AlertDialog
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
import pe.com.carwashperuapp.carwashapp.databinding.FragmentMyServicesBinding

class MyServicesFragment : Fragment(), MenuProvider {

    private var _binding: FragmentMyServicesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyServicesViewModel by activityViewModels {
        MyServicesViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.servicioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val servicesAdapter = ServicioListAdapter {
            viewModel.setSelectedService(it)
            ServiceBottomSheedDialog().show(childFragmentManager, ServiceBottomSheedDialog.TAG)
        }

        binding.rvServiceList.adapter = servicesAdapter

        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarServicios().collect() {
                servicesAdapter.submitList(it)
            }
        }

        binding.swipeService.setColorSchemeResources(R.color.purple)
        binding.swipeService.setOnRefreshListener {
            viewModel.descargarUsuarios()
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeService.isRefreshing = true
                else -> binding.swipeService.isRefreshing = false
            }
        }
        viewModel.goStatus.observe(viewLifecycleOwner) {
            when (it) {
                GoStatus.GO_ADD -> editarServicio()
                GoStatus.SHOW_DELETE -> showDeleteDialog()
                else -> {}
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.descargarUsuarios()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.service_delete)
            .setMessage(
                getString(
                    R.string.service_delete_msg,
                    viewModel.selectedService.value?.nombre
                )
            )
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.eliminarServicio()
                viewModel.clearEditStatus()
            }.setNegativeButton(R.string.cancel) { _, _ ->
                viewModel.clearEditStatus()
            }.show()
        viewModel.clearGoStatus()
    }

    fun editarServicio() {
        findNavController().navigate(R.id.action_nav_my_services_to_nav_add_service)
        viewModel.clearGoStatus()
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
            viewModel.goNuevo()
            return true
        }
        return false
    }

}