package pe.com.carwashperuapp.carwashapp.ui.reserve_list_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.DialogFiltroReservasFullBinding
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFecha

class DialogFiltroReservas : DialogFragment() {
    private var _binding: DialogFiltroReservasFullBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReserveListAdminViewModel by activityViewModels {
        ReserveListAdminViewModelFactory(
            SesionData(requireContext())
        )
    }

    private var fecha: androidx.core.util.Pair<Long, Long>? = null
    private var filtroDis: String? = null
    private var filtroCli: String? = null

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
        viewModel.apply {
            fecha = selectedFecha.value
            this@DialogFiltroReservas.filtroDis = filtroDis.value
            this@DialogFiltroReservas.filtroCli = filtroCli.value
        }

        binding.apply {
            fragment = this@DialogFiltroReservas
        }

        setFields()
    }

    private fun setFields() {
        binding.edtFecha.setText(
            if (fecha == null) "" else "${formatoFecha(fecha?.first!!)} - ${
                formatoFecha(
                    fecha?.second!!
                )
            }"
        )
        binding.edtNroDis.setText(filtroDis)
        binding.edtNroCli.setText(filtroCli)
    }

    fun close() {
        dismiss()
    }

    fun clear() {
        fecha = null
        filtroDis = null
        filtroCli = null
        setFields()
    }

    fun search() {
        filtroDis = binding.edtNroDis.text.toString()
        filtroCli = binding.edtNroCli.text.toString()
        viewModel.seleccionrFecha(fecha)
        viewModel.setFiltroDis(filtroDis)
        viewModel.setFiltroCli(filtroCli)
        dismiss()
    }

    fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Seleccion fecha")
                .setSelection(
                    fecha ?: androidx.core.util.Pair(
                        MaterialDatePicker.todayInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()
        datePicker.addOnPositiveButtonClickListener {
            fecha = it
            binding.edtFecha.setText(
                if (fecha == null) "" else "${formatoFecha(fecha?.first!!)} - ${
                    formatoFecha(
                        fecha?.second!!
                    )
                }"
            )
        }

        datePicker.show(childFragmentManager, "date_picker")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DialogFiltroReserva"
    }
}