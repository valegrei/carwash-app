package pe.com.carwashperuapp.carwashapp.ui.reserve_list_admin

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentMyReservesAdminBinding
import pe.com.carwashperuapp.carwashapp.model.Reserva
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFecha

class ReserveListAdminFragment : Fragment(), MenuProvider {

    private var _binding: FragmentMyReservesAdminBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ReserveListAdminViewModel by activityViewModels {
        ReserveListAdminViewModelFactory(
            SesionData(requireContext()),
        )
    }

    private lateinit var adapter: ReserveListAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReservesAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Adapter de RV
        adapter = ReserveListAdminAdapter { goView(it) }

        binding.rvReserveList.adapter = adapter

        binding.swipeReserves.setColorSchemeResources(R.color.purple)
        binding.swipeReserves.setOnRefreshListener {
            viewModel.consultarReservas()
        }
        viewModel.apply {
            reservas.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> binding.swipeReserves.isRefreshing = true
                    else -> binding.swipeReserves.isRefreshing = false
                }
            }
            selectedFecha.observe(viewLifecycleOwner) {
                manejarFiltroFecha(it)
            }
        }
        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun goView(reserva: Reserva) {
        viewModel.verReserva(reserva)
        findNavController().navigate(R.id.action_navigation_reserves_to_reserveDetailAdminFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun manejarFiltroFecha(it: androidx.core.util.Pair<Long,Long>?) {
        if (it != null) {
            setSubTitle("${formatoFecha(it.first)} - ${formatoFecha(it.second)}")
            showMenuClear()
            viewModel.consultarReservas()
        } else {
            setSubTitle(null)
            hideMenuClear()
            viewModel.consultarReservas()
        }
    }

    private var menuClear: MenuItem? = null
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_date, menu)
        menuClear = menu.findItem(R.id.action_clear)
        manejarFiltroFecha(viewModel.selectedFecha.value)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_search_date) {
            showFiltroDialog()
            return true
        } else if (menuItem.itemId == R.id.action_clear) {
            viewModel.limpiarFiltros()
            return true
        }
        return false
    }

    private fun showFiltroDialog() {
        val dialog = DialogFiltroReservas()
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun setSubTitle(subTitle: String?) {
        (activity as AppCompatActivity).supportActionBar?.subtitle = subTitle
    }

    private fun hideMenuClear() {
        menuClear?.isVisible = false
    }

    private fun showMenuClear() {
        menuClear?.isVisible = true
    }
}