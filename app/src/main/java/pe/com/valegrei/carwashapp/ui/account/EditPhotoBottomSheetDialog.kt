package pe.com.valegrei.carwashapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pe.com.valegrei.carwashapp.MainViewModel
import pe.com.valegrei.carwashapp.MainViewModelFactory
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.BottomsheetEditPhotoBinding

class EditPhotoBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomsheetEditPhotoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            SesionData(requireContext())
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

    fun nuevaFoto(){
        Toast.makeText(requireContext(),"nueva foto",Toast.LENGTH_SHORT).show()
        dismiss()
    }

    fun borrarFoto(){
        Toast.makeText(requireContext(),"Borrar",Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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