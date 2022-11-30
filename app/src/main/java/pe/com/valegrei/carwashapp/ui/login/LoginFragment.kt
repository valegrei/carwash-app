package pe.com.valegrei.carwashapp.ui.login

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
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.MainDisActivity
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.FragmentLoginBinding
import pe.com.valegrei.carwashapp.ui.util.ProgressDialog
import pe.com.valegrei.carwashapp.viewmodels.Status
import pe.com.valegrei.carwashapp.viewmodels.LoginViewModel
import pe.com.valegrei.carwashapp.viewmodels.LoginViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            (activity?.application as CarwashApplication).database.sesionDao(),
            (activity?.application as CarwashApplication).database.usuarioDao()
        )
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
        binding.apply {
            loginFragment = this@LoginFragment
            viewModel = loginViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        loginViewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> showLoading()
                Status.ERROR -> hideLoading()
                Status.SUCCESS -> {
                    hideLoading()
                    goMain()
                }
                Status.VERIFICAR -> {
                    hideLoading()
                    goVerify()
                }
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

    fun goVerify() {
        val action = LoginFragmentDirections
            .actionLoginFragmentToVerifyFragment(
                loginViewModel.usuario.value!!.id!!, loginViewModel.usuario.value!!.correo
            )
        findNavController().navigate(action)
    }

    fun goMain() {
        val intent = Intent(context, MainDisActivity::class.java)
        startActivity(intent)
    }

    var progressDialog = ProgressDialog()

    fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    fun hideLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
        }, 500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}