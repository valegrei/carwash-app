package pe.com.carwashperuapp.carwashapp.ui.reserves_list_cli

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
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveDetalleBinding
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

        val adapter = ServiceCliListAdapter()
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
            mostrarFavorito.observe(viewLifecycleOwner) {
                mostrarFav(it)
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

    override fun onPause() {
        super.onPause()
        viewModel.guardarFavorito()
    }

    private var deleteItem: MenuItem? = null
    private var menuAddFav: MenuItem? = null
    private var menuDelFav: MenuItem? = null
    private var menuCall: MenuItem? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.reserva_cli_detail_menu, menu)
        deleteItem = menu.findItem(R.id.action_delete)
        menuAddFav = menu.findItem(R.id.action_add_fav)
        menuDelFav = menu.findItem(R.id.action_del_fav)
        menuCall = menu.findItem(R.id.action_call)
        mostrarFav(viewModel.mostrarFavorito.value!!)
        mostrarLlamar()
        ocultarBoton()
    }

    private fun mostrarFav(mostrar: Boolean) {
        if (mostrar) {
            menuDelFav?.isVisible = true
            menuAddFav?.isVisible = false
        } else {
            menuDelFav?.isVisible = false
            menuAddFav?.isVisible = true
        }
    }

    private fun ocultarBoton() {
        val fechaHora = formatoFechaHoraDB(Date().time)
        val fechaHorario = viewModel.selectedReserva.value?.fechaHoraDB()!!
        deleteItem?.isVisible = fechaHorario > fechaHora
    }

    private fun mostrarLlamar() {
        menuCall?.isVisible = viewModel.mostrarLlamar() || viewModel.mostrarWhatsapp()
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_add_fav -> {
                viewModel.marcarFavorito()
                true
            }
            R.id.action_del_fav -> {
                viewModel.desmarcarFavorito()
                true
            }
            R.id.action_delete -> {
                anularReserva()
                true
            }
            R.id.action_navigation -> {
                mostrarRuta()
                true
            }
            R.id.action_call -> {
                llamar()
                true
            }
            else -> false
        }
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


    private fun mostrarRuta() {
        val latitude = viewModel.selectedReserva.value?.local?.latitud
        val longitude = viewModel.selectedReserva.value?.local?.longitud
        val url = "waze://?ll=$latitude, $longitude&navigate=yes"
        val intentWaze = Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.waze")

        val uriGoogle = "google.navigation:q=$latitude,$longitude"
        val intentGoogleNav = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uriGoogle)
        ).setPackage("com.google.android.apps.maps")

        val title: String = "Seleccione"
        val chooserIntent = Intent.createChooser(intentGoogleNav, title)
        val arr = arrayOfNulls<Intent>(1)
        arr[0] = intentWaze
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
        startActivity(chooserIntent)
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
        val nroCel = viewModel.selectedReserva.value?.distrib?.nroCel1
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$nroCel")
        return intent
    }

    private fun crearIntentWhatsapp(): Intent {
        val phone = viewModel.selectedReserva.value?.distrib?.nroCel2
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone=$phone"
        intent.setPackage("com.whatsapp")
        intent.data = Uri.parse(url)
        return intent
    }
}