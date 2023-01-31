package pe.com.carwashperuapp.carwashapp.ui.reserves_list_cli

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveDetalleBinding
import pe.com.carwashperuapp.carwashapp.ui.reserve.ServiceResumenListAdapter
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class ReserveDetailFragment : Fragment() {

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
        }
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
}