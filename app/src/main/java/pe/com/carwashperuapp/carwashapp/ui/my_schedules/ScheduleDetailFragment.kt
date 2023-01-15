package pe.com.carwashperuapp.carwashapp.ui.my_schedules

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.direccion.Direccion
import pe.com.carwashperuapp.carwashapp.databinding.FragmentScheduleDetailBinding


class ScheduleDetailFragment : Fragment(), MenuProvider {

    private var _binding: FragmentScheduleDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MySchedulesViewModel by activityViewModels {
        MySchedulesViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.horarioDao(),
            (activity?.application as CarwashApplication).database.direccionDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@ScheduleDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            fragment = this@ScheduleDetailFragment
        }
        binding.acLocal.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedLocal = adapterView.getItemAtPosition(position) as Direccion
            viewModel.setSelectedLocal(selectedLocal)
        }
        viewModel.apply {
            locales.observe(viewLifecycleOwner) {
                binding.acLocal.setText(local.value?.toString())
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
                binding.acLocal.setAdapter(adapter)
            }
            editStatus.observe(viewLifecycleOwner) {
                mostrarTitulo(it)
                mostrarBotones(it)
                salir(it)
            }
        }


        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    fun salir(editStatus: EditStatus) {
        if (editStatus == EditStatus.EXIT)
            findNavController().popBackStack()
    }

    fun mostrarTitulo(status: EditStatus) {
        when (status) {
            EditStatus.VIEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.schedules_title_view)
            EditStatus.EDIT -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.schedules_title_edit)
            EditStatus.NEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.schedules_title_new)
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var deleteItem: MenuItem? = null
    private var editItem: MenuItem? = null
    private var saveItem: MenuItem? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_item_menu, menu)
        deleteItem = menu.findItem(R.id.action_delete)
        editItem = menu.findItem(R.id.action_edit)
        saveItem = menu.findItem(R.id.action_save)
        mostrarBotones(viewModel.editStatus.value!!)
    }

    fun mostrarBotones(status: EditStatus) {
        when (status) {
            EditStatus.VIEW -> {
                deleteItem?.isVisible = true
                editItem?.isVisible = true
                saveItem?.isVisible = false
            }
            EditStatus.EDIT, EditStatus.NEW -> {
                deleteItem?.isVisible = false
                editItem?.isVisible = false
                saveItem?.isVisible = true
            }
            else -> {}
        }
    }

    fun mostrarDialogoDias() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(R.string.schedules_label_freq)
        val items = arrayOf(
            getString(R.string.schedules_freq_lunes),
            getString(R.string.schedules_freq_martes),
            getString(R.string.schedules_freq_miercoles),
            getString(R.string.schedules_freq_jueves),
            getString(R.string.schedules_freq_viernes),
            getString(R.string.schedules_freq_sabado),
            getString(R.string.schedules_freq_domingo),
        )
        val checkedItems = booleanArrayOf(
            viewModel.lunes.value ?: false,
            viewModel.martes.value ?: false,
            viewModel.miercoles.value ?: false,
            viewModel.jueves.value ?: false,
            viewModel.viernes.value ?: false,
            viewModel.sabado.value ?: false,
            viewModel.domingo.value ?: false,
        )
        alertDialog.setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }
        alertDialog.setPositiveButton(R.string.accept) { _, _ ->
            viewModel.setPersonalizado(checkedItems)
        }
        alertDialog.setNegativeButton(R.string.cancel, null)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun mostrarRelojIni() {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(viewModel.horaIni.value!!)
                .setMinute(viewModel.minIni.value!!)
                .setTitleText(R.string.schedules_label_start_sel)
                .setPositiveButtonText(R.string.accept)
                .setNegativeButtonText(R.string.cancel)
                .build()
        picker.isCancelable = false
        picker.addOnPositiveButtonClickListener {
            viewModel.setHorarioIni(picker.hour, picker.minute)
        }
        picker.show(childFragmentManager, "relojIniDialog")
    }

    fun mostrarRelojFin() {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(viewModel.horaFin.value!!)
                .setMinute(viewModel.minFin.value!!)
                .setTitleText(R.string.schedules_label_start_sel)
                .setPositiveButtonText(R.string.accept)
                .setNegativeButtonText(R.string.cancel)
                .build()
        picker.isCancelable = false
        picker.addOnPositiveButtonClickListener {
            viewModel.setHorarioFin(picker.hour, picker.minute)
        }
        picker.show(childFragmentManager, "relojFinDialog")
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                showEliminar()
                true
            }
            R.id.action_edit -> {
                viewModel.editarHorario()
                true
            }
            R.id.action_save -> {
                showGuardar()
                true
            }
            else -> false
        }
    }

    private fun showEliminar() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.schedules_delete)
            .setMessage(R.string.schedules_delete_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.eliminarHorarioConfig()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showGuardar() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.schedules_save)
            .setMessage(R.string.schedules_save_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.guardarHorarioConfig()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}