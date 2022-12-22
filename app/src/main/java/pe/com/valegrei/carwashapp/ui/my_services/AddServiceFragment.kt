package pe.com.valegrei.carwashapp.ui.my_services

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentAddServiceBinding
import pe.com.valegrei.carwashapp.ui.util.ProgressDialog

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
    ): View? {
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
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> showLoading()
                Status.ERROR -> hideLoading()
                Status.SUCCESS -> exit()
                else -> {}
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
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