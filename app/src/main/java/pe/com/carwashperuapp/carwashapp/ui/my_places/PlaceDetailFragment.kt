package pe.com.carwashperuapp.carwashapp.ui.my_places

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Departamento
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Distrito
import pe.com.carwashperuapp.carwashapp.database.ubigeo.Provincia
import pe.com.carwashperuapp.carwashapp.databinding.FragmentPlaceDetailBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class PlaceDetailFragment : Fragment(), MenuProvider {

    private var _binding: FragmentPlaceDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MyPlacesViewModel by activityViewModels {
        MyPlacesViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.ubigeoDao(),
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@PlaceDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            fragment = this@PlaceDetailFragment
        }

        binding.acDepartamentos.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedDepartamento = adapterView.getItemAtPosition(position) as Departamento
            viewModel.setSelectedDepartamento(selectedDepartamento)
            val noProv = Provincia(0, "", 0)
            viewModel.setSelectedProvincia(noProv)
            viewModel.cargarProvincias(selectedDepartamento)
            val noDis = Distrito(0, "", 0, "")
            viewModel.setSelectedDistrito(noDis)
            viewModel.cargarDistritos(noProv)
        }
        binding.acProvincias.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedProvincia = adapterView.getItemAtPosition(position) as Provincia
            viewModel.setSelectedProvincia(selectedProvincia)
            viewModel.setSelectedDistrito(Distrito(0, "", 0, ""))
            viewModel.cargarDistritos(selectedProvincia)
        }
        binding.acDistritos.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedDistrito = adapterView.getItemAtPosition(position) as Distrito
            viewModel.setSelectedDistrito(selectedDistrito)
        }

        viewModel.apply {
            departamentos.observe(viewLifecycleOwner) {
                binding.acDepartamentos.setText(selectedDepartamento.value?.toString())
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
                binding.acDepartamentos.setAdapter(adapter)
            }
            provincias.observe(viewLifecycleOwner) {
                binding.acProvincias.setText(selectedProvincia.value?.toString())
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
                binding.acProvincias.setAdapter(adapter)
            }
            distritos.observe(viewLifecycleOwner) {
                binding.acDistritos.setText(selectedDistrito.value?.toString())
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
                binding.acDistritos.setAdapter(adapter)
            }
            editStatus.observe(viewLifecycleOwner) {
                mostrarTitulo(it)
                mostrarBotones(it)
                salir(it)
            }
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> showLoading()
                    Status.ERROR, Status.SUCCESS -> hideLoading()
                    else -> {}
                }
            }
            errDepartamento.observe(viewLifecycleOwner) {
                binding.tiDepartamento.error = it
            }
            errProvincia.observe(viewLifecycleOwner) {
                binding.tiProvincia.error = it
            }
            errDistrito.observe(viewLifecycleOwner) {
                binding.tiDistrito.error = it
            }
            errDireccion.observe(viewLifecycleOwner) {
                binding.tiDireccion.error = it
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
                getString(R.string.place_title_view)
            EditStatus.EDIT -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.place_title_edit)
            EditStatus.NEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.place_title_new)
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

    fun showMapOrEdit() {
        val direccion = viewModel.selectedDireccion.value
        if (viewModel.editStatus.value == EditStatus.VIEW) {
            // Display a label at the location of Google's Sydney office
            val gmmIntentUri =
                Uri.parse("geo:${direccion?.latitud},${direccion?.longitud}?z=16&q=${direccion?.latitud},${direccion?.longitud}(${direccion?.direccion})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        } else {
            findNavController().navigate(R.id.action_nav_place_detail_to_nav_add_place)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                showEliminar()
                true
            }
            R.id.action_edit -> {
                viewModel.editarDireccion()
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
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.place_delete)
            .setMessage(R.string.place_delete_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.eliminarDireccion()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showGuardar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.place_save)
            .setMessage(R.string.place_save_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.guardarDireccion()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    var progressDialog = ProgressDialog()

    fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    fun hideLoading() {
        if (progressDialog.isVisible)
            progressDialog.dismiss()
    }
}