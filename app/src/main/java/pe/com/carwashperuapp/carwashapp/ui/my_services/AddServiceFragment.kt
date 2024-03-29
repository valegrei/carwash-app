package pe.com.carwashperuapp.carwashapp.ui.my_services

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
import pe.com.carwashperuapp.carwashapp.database.servicio.Duracion
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoDocumento
import pe.com.carwashperuapp.carwashapp.databinding.FragmentAddServiceBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class AddServiceFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAddServiceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyServicesViewModel by activityViewModels {
        MyServicesViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.servicioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@AddServiceFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.acDuracion.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedDuracion = adapterView.getItemAtPosition(position) as Duracion
            viewModel.duracion.value = selectedDuracion
        }
        viewModel.apply {
            binding.acDuracion.setText(duracion.value.toString())
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf(
                    Duracion.MIN_15,
                    Duracion.MIN_30,
                    Duracion.MIN_45,
                    Duracion.MIN_60,
                    Duracion.MIN_90,
                    Duracion.MIN_120,
                    Duracion.MIN_150,
                    Duracion.MIN_180,
                )
            )
            binding.acDuracion.setAdapter(adapter)
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> showLoading()
                    Status.SUCCESS -> exit()
                    else -> hideLoading()
                }
            }

        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
        (activity as AppCompatActivity).supportActionBar?.title =
            if (viewModel.editStatus.value == EditStatus.NEW) getString(R.string.service_add) else getString(R.string.service_edit)
    }

    var progressDialog = ProgressDialog()

    private fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    private fun hideLoading() {
        if (progressDialog.isVisible)
            progressDialog.dismiss()
    }

    private fun exit() {
        if (progressDialog.isVisible)
            progressDialog.dismiss()
        findNavController().popBackStack()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.save_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_save) {
            viewModel.guardar()
            return true
        }
        return false
    }

}