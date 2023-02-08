package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveBinding
import pe.com.carwashperuapp.carwashapp.model.Horario

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        val adapter = ServiceListAdapter()
        binding.rvServices.adapter = adapter

        viewModel.apply {
            servicios.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            horarios.observe(viewLifecycleOwner) { it ->
                cargarChips(it)
            }
            errMsg.observe(viewLifecycleOwner) {
                //showErrMsg(it)
                showSnackBar(it)
            }
        }
        binding.chgHorarios.setOnCheckedStateChangeListener { group, _ ->
            val seleccionado = viewModel.horariosMap.value?.get(group.checkedChipId)
            viewModel.seleccionarHorario(seleccionado)
        }
    }

    private fun cargarChips(horarios: List<Horario>?) {
        binding.chgHorarios.removeAllViews()
        viewModel.seleccionarHorario(null)
        val map = mutableMapOf<Int, Horario>()
        if (horarios.isNullOrEmpty()) {
            binding.tvNoHorarios.visibility = View.VISIBLE
            binding.chgHorarios.visibility = View.GONE
        } else {
            horarios.forEach {
                val chip = Chip(requireContext())
                chip.text = it.toString()
                chip.isCheckable = true
                binding.chgHorarios.addView(chip)
                map[chip.id] = it
            }
            binding.tvNoHorarios.visibility = View.GONE
            binding.chgHorarios.visibility = View.VISIBLE
        }
        viewModel.setHorarioMap(map)
    }

    fun reservar() {
        viewModel.seleccionadosServicios()
        cargarHorarioSeleccionado()
        viewModel.reservar()
    }

    private fun cargarHorarioSeleccionado() {
        val seleccionado = viewModel.horariosMap.value?.get(binding.chgHorarios.checkedChipId)
        viewModel.seleccionarHorario(seleccionado)
    }

    //Muestra el selector de fechas
    fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Seleccion fecha")
            .setSelection(viewModel.selectedFecha.value).build()
        datePicker.addOnPositiveButtonClickListener {
            if (MaterialDatePicker.todayInUtcMilliseconds() <= it) {
                viewModel.seleccionarFecha(it)
            } else {
                Snackbar.make(
                    binding.root, "Debe seleccionar una fecha futura", Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSnackBar(msg: String?) {
        if (!msg.isNullOrEmpty()) {
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            viewModel.clearErr()
        }
    }

    fun mostrarSeleccionarVehiculo() {
        val adapter = VehiculoAdapter(requireContext(), viewModel.vehiculos.value!!)
        AlertDialog.Builder(requireContext()).setAdapter(adapter) { _, which ->
            val seleccionado = adapter.getItem(which)
            viewModel.seleccionarVehiculo(seleccionado!!)
        }.setTitle("Seleccionar Vehículo").show()
    }
}