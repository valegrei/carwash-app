package pe.com.valegrei.carwashapp.ui.register

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.FragmentRegisterBinding
import pe.com.valegrei.carwashapp.ui.util.ProgressDialog

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Configura Toolbar con Navigation Component
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = registerViewModel
        }

        registerViewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> showLoading()
                Status.ERROR -> hideLoading()
                Status.GO_VERIFY -> goVerify()
                Status.GO_LOGIN -> goLogin()
                else -> {}
            }
        }
    }

    private fun goVerify() {
        Handler(Looper.getMainLooper()).postDelayed({
            registerViewModel.clear()
            progressDialog.dismiss()
            val action = RegisterFragmentDirections
                .actionRegisterFragmentToVerifyFragment(
                    registerViewModel.usuario.value?.id!!,
                    registerViewModel.usuario.value?.correo!!
                )
            findNavController().navigate(action)
        }, 200)
    }

    private fun goLogin() {
        registerViewModel.clear()
        progressDialog.dismiss()
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            //.setTitle(R.string.login_act_dist_title)
            .setMessage(R.string.login_act_dist_msg)
            .setPositiveButton(R.string.accept) { _, _ ->
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}