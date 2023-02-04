package pe.com.carwashperuapp.carwashapp.ui.reserves_list_cli

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveDetalleBinding
import pe.com.carwashperuapp.carwashapp.ui.reserve.ServiceResumenListAdapter
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaHoraDB
import java.util.*

class ReserveDetailFragment : Fragment(), MenuProvider {

    private var _binding: FragmentReserveDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyReserveViewModel by activityViewModels {
        MyReserveViewModelFactory(
            SesionData(requireContext()),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@ReserveDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val adapter = ServiceResumenListAdapter()
        binding.rvServices.adapter = adapter

        viewModel.apply {
            selectedReserva.observe(viewLifecycleOwner) {
                adapter.submitList(it.servicioReserva)
            }
            errMsg.observe(viewLifecycleOwner) {
                showErrMsg(it)
            }
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> hideLoading()
                    else -> {}
                }
            }
            editStatus.observe(viewLifecycleOwner){
                when(it){
                    EditStatus.EXIT -> exit()
                    else -> {}
                }
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

    fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    fun hideLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (progressDialog.isVisible)
                progressDialog.dismiss()
        }, 500)
    }

    private fun exit() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (progressDialog.isVisible)
                progressDialog.dismiss()
            findNavController().popBackStack()
        }, 500)
    }

    private fun showErrMsg(errMsg: String?) {
        if ((errMsg ?: "").isNotEmpty())
            Snackbar.make(binding.root, errMsg!!, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var deleteItem: MenuItem? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.delete_item_menu, menu)
        deleteItem = menu.findItem(R.id.action_delete)
        ocultarBoton()
    }

    private fun ocultarBoton() {
        val fechaHora = formatoFechaHoraDB(Date().time)
        val fechaHorario = viewModel.selectedReserva.value?.horario?.fechaHoraDB()!!
        deleteItem?.isVisible = fechaHorario > fechaHora
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return if (menuItem.itemId == R.id.action_delete) {
            anularReserva()
            true
        } else false
    }

    private fun anularReserva() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.reserve_delete)
            .setMessage(R.string.reserve_delete_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.anularReserva()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}