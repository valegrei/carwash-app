package pe.com.carwashperuapp.carwashapp.ui.reserve_list_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.DialogFiltroReservasFullBinding

class DialogFiltroReservas : DialogFragment() {
    private var _binding: DialogFiltroReservasFullBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReserveListAdminViewModel by activityViewModels {
        ReserveListAdminViewModelFactory(
            SesionData(requireContext())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_CarwashApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFiltroReservasFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@DialogFiltroReservas
        }
    }

    fun close(){
        dismiss()
    }

    fun clear(){

    }

    fun search(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DialogFiltroReserva"
    }
}