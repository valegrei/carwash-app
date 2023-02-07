package pe.com.carwashperuapp.carwashapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pe.com.carwashperuapp.carwashapp.MainViewModel
import pe.com.carwashperuapp.carwashapp.MainViewModelFactory
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.BottomsheetEditPhotoBannerBinding

class EditPhotoBannerBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomsheetEditPhotoBannerBinding? = null
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
    ): View {
        _binding = BottomsheetEditPhotoBannerBinding.inflate(inflater, container, false);
        return binding.root
    }

    fun nuevaFoto() {
        viewModel.lanzarAddFoto()
        dismiss()
    }

    fun borrarFoto() {
        dismiss()
        viewModel.eliminarFoto()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.fragment = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "EditPhotoBannerBottomSheedDialog"
    }
}