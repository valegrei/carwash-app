package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveResumenBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class ReserveResumenFragment : Fragment() {

    private var _binding: FragmentReserveResumenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@ReserveResumenFragment
            viewModel = this@ReserveResumenFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val adapter = ServiceResumenListAdapter()
        binding.rvServices.adapter = adapter

        viewModel.apply {
            selectedServicios.observe(viewLifecycleOwner) {
                adapter.submitList(it)
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
            goStatus.observe(viewLifecycleOwner) {
                if (it == GoStatus.GO_LIST)
                    goReserveList()
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

    private fun goReserveList() {
        viewModel.clearGoStatus()
        Handler(Looper.getMainLooper()).postDelayed({
            if (progressDialog.isVisible)
                progressDialog.dismiss()
            findNavController().navigate(R.id.action_reserveResumenFragment_to_nav_my_reserves)
        }, 500)
    }

    private fun showErrMsg(errMsg: String?) {
        if (errMsg.isNullOrEmpty()) {
            binding.tvErrMsg.text = null
            binding.tvErrMsg.visibility = View.GONE
        } else {
            binding.tvErrMsg.text = errMsg
            binding.tvErrMsg.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}