package pe.com.valegrei.carwashapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pe.com.valegrei.carwashapp.databinding.BottomsheetEditPhotoBinding

class EditPhotoBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomsheetEditPhotoBinding? = null
    private val binding get() = _binding!!
    /*private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            SesionData(requireContext())
        )
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetEditPhotoBinding.inflate(inflater, container, false);
        return binding.root
    }

    fun nuevaFoto() {
        //viewModel.lanzarAddFoto()
        dismiss()
    }

    fun borrarFoto() {
        dismiss()
        //viewModel.eliminarFoto()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.viewModel = viewModel
        binding.fragment = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "EditPhotoBottomSheedDialog"
    }
}