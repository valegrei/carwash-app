package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.BottomsheetLocalBinding

class LocalBottomSheedDialog : BottomSheetDialogFragment() {
    private var _binding: BottomsheetLocalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetLocalBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mostrarCerrrarServicios()
        val adapter = ServiceOfferedListAdapter()
        binding.apply {
            viewModel = this@LocalBottomSheedDialog.viewModel
            lifecycleOwner = viewLifecycleOwner
            fragment = this@LocalBottomSheedDialog
            rvServicesOffered.adapter = adapter
        }

        viewModel.apply {
            val services = selectedLocal.value?.distrib?.servicios
            adapter.submitList(services)
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> dismiss()
                    else -> {}
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "LocalBottomSheedDialog"
    }
}