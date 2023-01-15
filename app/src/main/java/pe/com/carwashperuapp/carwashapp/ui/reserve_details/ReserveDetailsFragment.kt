package pe.com.carwashperuapp.carwashapp.ui.reserve_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveDetailsBinding
import pe.com.carwashperuapp.carwashapp.model.Servicio

class ReserveDetailsFragment : Fragment(), ReserveDetailsAdapter.OnInteractionListener {

    private var _binding: FragmentReserveDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReserveDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reserveDetailsViewModel =
            ViewModelProvider(this)[ReserveDetailsViewModel::class.java]
        reserveDetailsViewModel.reservaItems.observe(viewLifecycleOwner){
            binding.tvClient.text = it.client
            binding.tvVehicle.text = it.vehicle
            binding.tvPlace.text = it.place
        }
        reserveDetailsViewModel.reserveDetails.observe(viewLifecycleOwner) {
            binding.rvReserveDetail.adapter = ReserveDetailsAdapter(requireContext(), it, this)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(item: Servicio) {

    }

}