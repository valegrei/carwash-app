package pe.com.valegrei.carwashapp.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.parametro.*
import pe.com.valegrei.carwashapp.databinding.AlertEdittextBinding
import pe.com.valegrei.carwashapp.databinding.FragmentSettingsBinding
import pe.com.valegrei.carwashapp.ui.util.ProgressDialog

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.parametroDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@SettingsFragment
            viewModel = this@SettingsFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarParametros().collect() {
                viewModel.setParametros(it)
            }
        }

        binding.swipeSettings.setColorSchemeResources(R.color.purple)

        binding.swipeSettings.setOnRefreshListener {
            viewModel.descargarParams()
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> mostrarSwipeLoading()
                Status.LOADING_BLOCK -> showLoading()
                Status.ERROR -> mostrarError()
                else -> ocultarSwipeLoading()
            }
        }

        viewModel.descargarParams()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun mostrarSwipeLoading() {
        binding.swipeSettings.isRefreshing = true
    }

    fun ocultarSwipeLoading() {
        binding.swipeSettings.isRefreshing = false
        hideLoading()
    }

    fun mostrarError() {
        ocultarSwipeLoading()
        Snackbar.make(binding.lyScroll, viewModel.errMsg.value ?: "", Snackbar.LENGTH_LONG).show()
    }

    fun editarHost() {
        val bindEdt = AlertEdittextBinding.inflate(layoutInflater)
        val text = viewModel.parametros.value?.get(EMAIL_HOST)
        bindEdt.edittext.append(text ?: "")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_lbl_smtp_host)
            .setView(bindEdt.root)
            .setCancelable(true)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setHost(bindEdt.edittext.text.toString().trim())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun editarPort() {
        val bindEdt = AlertEdittextBinding.inflate(layoutInflater)
        val text = viewModel.parametros.value?.get(EMAIL_PORT)
        bindEdt.edittext.inputType = InputType.TYPE_CLASS_NUMBER
        bindEdt.edittext.append(text ?: "")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_lbl_smtp_port)
            .setView(bindEdt.root)
            .setCancelable(true)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setPort(bindEdt.edittext.text.toString().trim())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }


    fun editarSecure() {
        val secure = viewModel.parametros.value?.get(EMAIL_SSL_TLS) ?: "0"
        var selected = secure.toInt()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_lbl_smtp_secure)
            .setSingleChoiceItems(arrayOf("TLS", "SSL"), selected) { _, which ->
                selected = which
            }
            .setCancelable(true)
            .setPositiveButton(R.string.accept) { d, _ ->
                viewModel.setSecure(selected)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }


    fun editarAddr() {
        val bindEdt = AlertEdittextBinding.inflate(layoutInflater)
        val text = viewModel.parametros.value?.get(EMAIL_ADDR)
        bindEdt.edittext.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        bindEdt.edittext.append(text ?: "")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_lbl_email_address)
            .setView(bindEdt.root)
            .setCancelable(true)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setAddress(bindEdt.edittext.text.toString().trim())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun editarPass() {
        val bindEdt = AlertEdittextBinding.inflate(layoutInflater)
        val text = viewModel.parametros.value?.get(EMAIL_PASS)
        bindEdt.edittext.append(text ?: "")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_lbl_email_pass)
            .setView(bindEdt.root)
            .setCancelable(true)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setPass(bindEdt.edittext.text.toString().trim())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private var progressDialog = ProgressDialog()

    private fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    private fun hideLoading() {
        if (progressDialog.isVisible)
            progressDialog.dismiss()
    }
}