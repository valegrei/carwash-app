package pe.com.carwashperuapp.carwashapp.ui.reserve_list_dis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentMyReservesDisBinding
import pe.com.carwashperuapp.carwashapp.model.Reserva

class ReserveListFragment : Fragment() {

    private var _binding: FragmentMyReservesDisBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ReserveListViewModel by activityViewModels {
        ReserveListViewModelFactory(
            SesionData(requireContext()),
        )
    }

    private lateinit var adapter: ReserveListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReservesDisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Adapter de RV
        adapter = ReserveListAdapter { goView(it) }

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
        }
        viewModel.consultarReservas()

    }

    private fun goView(reserva: Reserva) {
        viewModel.verReserva(reserva)
        findNavController().navigate(R.id.action_nav_reserve_list_to_serviceDetailsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}