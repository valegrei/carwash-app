package pe.com.carwashperuapp.carwashapp.ui.login

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
import pe.com.carwashperuapp.carwashapp.*
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentLoginBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(SesionData(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shoVersion()
        binding.apply {
            loginFragment = this@LoginFragment
            viewModel = loginViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        loginViewModel.status.observe(viewLifecycleOwner) {
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

    fun login() {
        loginViewModel.login()
    }

    fun goRecoverPass() {
        findNavController().navigate(R.id.action_loginFragment_to_recoverFragment)
    }

    fun goRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun goVerify() {
        Handler(Looper.getMainLooper()).postDelayed({
            loginViewModel.clear()
            progressDialog.dismiss()
            val action = LoginFragmentDirections
                .actionLoginFragmentToVerifyFragment(
                    loginViewModel.usuario.value!!.id!!, loginViewModel.usuario.value!!.correo
                )
            findNavController().navigate(action)
        }, 500)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shoVersion(){
        val versionName = BuildConfig.VERSION_NAME
        binding.tvVersion.text = getString(R.string.login_app_version,versionName)
    }
}