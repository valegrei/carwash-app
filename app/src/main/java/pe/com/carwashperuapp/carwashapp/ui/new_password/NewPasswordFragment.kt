package pe.com.carwashperuapp.carwashapp.ui.new_password

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import pe.com.carwashperuapp.carwashapp.MainAdminActivity
import pe.com.carwashperuapp.carwashapp.MainCliActivity
import pe.com.carwashperuapp.carwashapp.MainDisActivity
import pe.com.carwashperuapp.carwashapp.SincroCliService
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentNewPasswordBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class NewPasswordFragment : Fragment() {
    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    private val args: NewPasswordFragmentArgs by navArgs()

    private val newPasswordViewModel: NewPasswordViewModel by viewModels {
        NewPasswordViewModelFactory(SesionData(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = newPasswordViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        newPasswordViewModel.apply {
            cargarDatos(args.correo)
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> hideLoading()
                    Status.GO_ADMIN -> goAdmin()
                    Status.GO_CLIENT -> goClient()
                    Status.GO_DISTR -> goDistr()
                    Status.VERIFICAR -> goVerify()
                    else -> {}
                }
            }
        }
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

    private fun goVerify() {
        Handler(Looper.getMainLooper()).postDelayed({
            newPasswordViewModel.clear()
            progressDialog.dismiss()
            val action = NewPasswordFragmentDirections
                .actionNewPasswordFragmentToVerifyFragment(
                    newPasswordViewModel.usuario.value!!.id!!,
                    newPasswordViewModel.usuario.value!!.correo
                )
            findNavController().navigate(action)
        }, 500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}