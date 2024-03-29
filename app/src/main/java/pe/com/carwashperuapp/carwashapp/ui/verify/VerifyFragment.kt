package pe.com.carwashperuapp.carwashapp.ui.verify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import pe.com.carwashperuapp.carwashapp.*
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentVerifyBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class VerifyFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private val verifyViewModel: VerifyViewModel by viewModels {
        VerifyViewModelFactory(SesionData(requireContext()))
    }

    private val args: VerifyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Carga datos

        verifyViewModel.apply {
            cargarDatos(args.idUsuario, args.correo)
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> hideLoading()
                    Status.SENT_CODE -> showSentCode()
                    Status.GO_ADMIN -> goAdmin()
                    Status.GO_CLIENT -> goClient()
                    Status.GO_DISTR -> goDistr()
                    else -> {}
                }
            }
        }

        binding.apply {
            viewModel = verifyViewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goAdmin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(context, MainAdminActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, 500)
    }

    private fun goClient() {
        val intentSrv = Intent(requireContext(), SincroCliService::class.java)
        requireActivity().startService(intentSrv)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(context, MainCliActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, 500)
    }

    private fun goDistr() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(context, MainDisActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, 500)
    }


    private var progressDialog = ProgressDialog()

    private fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    private fun hideLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
        }, 500)
    }

    private fun showSentCode() {
        Handler(Looper.getMainLooper()).postDelayed({
            verifyViewModel.clearDialogs()
            progressDialog.dismiss()
            Snackbar.make(binding.lyVerify, R.string.verify_sent_code, Snackbar.LENGTH_LONG).show()
        }, 500)
    }

}