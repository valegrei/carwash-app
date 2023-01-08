package pe.com.valegrei.carwashapp.ui.my_schedules

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
import pe.com.valegrei.carwashapp.database.horario.HorarioConfig
import pe.com.valegrei.carwashapp.databinding.FragmentMySchedulesBinding

class MySchedulesFragment : Fragment(), MenuProvider {

    private var _binding: FragmentMySchedulesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: MySchedulesListAdapter


    private val viewModel: MySchedulesViewModel by activityViewModels {
        MySchedulesViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.horarioDao(),
            (activity?.application as CarwashApplication).database.direccionDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MySchedulesListAdapter { goView(it) }
        binding.rvSchedulesList.adapter = adapter
        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarHorarioConfigs().collect {
                adapter.submitList(it)
            }
        }
        binding.swipeSchedules.setColorSchemeResources(R.color.purple)
        binding.swipeSchedules.setOnRefreshListener {
            viewModel.descargarHorarioConfigs()
        }
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeSchedules.isRefreshing = true
                else -> binding.swipeSchedules.isRefreshing = false
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
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
            viewModel.nuevoHorarioConfig()
            findNavController().navigate(R.id.action_nav_my_schedules_to_nav_schedules_detail)
            return true
        }
        return false
    }

    private fun goView(horarioConfig: HorarioConfig) {
        viewModel.verHorario(horarioConfig)
        findNavController().navigate(R.id.action_nav_my_schedules_to_nav_schedules_detail)
    }
}