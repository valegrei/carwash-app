package pe.com.carwashperuapp.carwashapp.ui.distribs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.BottomsheetDistribBinding

class DistribsBottomSheedDialog : BottomSheetDialogFragment() {

    private var _binding: BottomsheetDistribBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DistribsViewModel by activityViewModels {
        DistribsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.usuarioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetDistribBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> dismiss()
                else -> {}
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "DistribsBottomSheedDialog"
    }
}