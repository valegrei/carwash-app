package pe.com.carwashperuapp.carwashapp.ui.my_cars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.BottomsheetEditPhotoBinding

class EditPhotoBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomsheetEditPhotoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyCarsViewModel by activityViewModels {
        MyCarsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetEditPhotoBinding.inflate(inflater, container, false);
        return binding.root
    }

    fun nuevaFoto() {
        dismiss()
        viewModel.lanzarAddFoto()
    }

    fun borrarFoto() {
        dismiss()
        viewModel.eliminarFoto()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        binding.viewModel = viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "EditPhotoBottomSheedDialog"
    }
}