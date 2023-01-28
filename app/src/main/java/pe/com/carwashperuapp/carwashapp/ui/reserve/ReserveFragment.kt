package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveBinding
import java.util.*

class ReserveFragment : Fragment() {

    private var _binding: FragmentReserveBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@ReserveFragment
            viewModel = this@ReserveFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val adapter = ServiceListAdapter() {}
        binding.rvServices.adapter = adapter

        viewModel.apply {
            servicios.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }
    //Muestra el selector de fechas
    fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccion fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            if(Date().time < it) {
                viewModel.seleccionarFecha(Date(it))
            }else{
                Snackbar.make(binding.root, "Debe seleccionar una fecha futura",Snackbar.LENGTH_SHORT).show()
            }
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun mostrarSeleccionarVehiculo() {
        val adapter = VehiculoAdapter(requireContext(), viewModel.vehiculos.value!!)
        AlertDialog.Builder(requireContext())
            .setAdapter(adapter) { _, which ->
                val seleccionado = adapter.getItem(which)
                viewModel.seleccionarVehiculo(seleccionado!!)
            }
            .setTitle("Seleccionar Vehículo")
            .show()
    }
}