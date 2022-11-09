package pe.com.valegrei.carwashapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.MainDisActivity
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
        showVersion()
    }

    fun showVersion() {
        binding.apply {
            loginFragment = this@LoginFragment
        }
    }

    fun goRecoverPass() {
        findNavController().navigate(R.id.action_loginFragment_to_recoverFragment)
    }

    fun goRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun goMain(){
        val intent = Intent(context, MainDisActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}