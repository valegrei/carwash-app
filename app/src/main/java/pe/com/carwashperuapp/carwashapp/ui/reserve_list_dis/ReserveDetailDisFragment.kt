package pe.com.carwashperuapp.carwashapp.ui.reserve_list_dis

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveDetalleDisBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaLimaDB
import java.util.*

class ReserveDetailDisFragment : Fragment(), MenuProvider {

    private var _binding: FragmentReserveDetalleDisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReserveListViewModel by activityViewModels {
        ReserveListViewModelFactory(
            SesionData(requireContext()),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveDetalleDisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@ReserveDetailDisFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val adapter = ServiceDisListAdapter(seMuestraBoton())
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
            editStatus.observe(viewLifecycleOwner) {
                when (it) {
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


    private var saveItem: MenuItem? = null
    private var menuCall: MenuItem? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.reserva_dis_detail_menu, menu)
        saveItem = menu.findItem(R.id.action_save)
        menuCall = menu.findItem(R.id.action_call)
        ocultarBoton()
        mostrarLlamar()
    }

    private fun seMuestraBoton(): Boolean {
        val fechaHora = formatoFechaLimaDB(Date().time)
        val fechaHorario = viewModel.selectedReserva.value?.fecha!!
        return fechaHorario >= fechaHora
    }

    private fun ocultarBoton() {
        saveItem?.isVisible = seMuestraBoton()
    }

    private fun mostrarLlamar() {
        menuCall?.isVisible = viewModel.mostrarLlamar() || viewModel.mostrarWhatsapp()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return if (menuItem.itemId == R.id.action_save) {
            guardarReserva()
            true
        } else if (menuItem.itemId == R.id.action_call) {
            llamar()
            true
        } else false
    }

    private fun guardarReserva() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.reserve_save)
            .setMessage(R.string.reserve_save_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.editarReserva()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun llamar() {
        if (viewModel.mostrarLlamar() && viewModel.mostrarWhatsapp()) {
            val intentLlamar = crearIntentLlamar()
            val intentWhatsapp = crearIntentWhatsapp()
            val title = "Seleccione"
            val chooserIntent = Intent.createChooser(intentLlamar, title)
            val arr = arrayOfNulls<Intent>(1)
            arr[0] = intentWhatsapp
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
            startActivity(chooserIntent)
        }else if(viewModel.mostrarLlamar()){
            startActivity(crearIntentLlamar())
        }else if(viewModel.mostrarWhatsapp()){
            startActivity(crearIntentWhatsapp())
        }
    }

    private fun crearIntentLlamar(): Intent {
        val nroCel = viewModel.selectedReserva.value?.cliente?.nroCel1
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$nroCel")
        return intent
    }

    private fun crearIntentWhatsapp(): Intent {
        val phone = viewModel.selectedReserva.value?.cliente?.nroCel2
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone=$phone"
        intent.setPackage("com.whatsapp")
        intent.data = Uri.parse(url)
        return intent
    }
}