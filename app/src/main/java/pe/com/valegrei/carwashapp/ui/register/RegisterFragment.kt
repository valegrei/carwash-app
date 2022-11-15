package pe.com.valegrei.carwashapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pe.com.valegrei.carwashapp.MainDisActivity
import pe.com.valegrei.carwashapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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
            registerFragment = this@RegisterFragment
        }
    }

    fun goMain() {
        val intent = Intent(context, MainDisActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}