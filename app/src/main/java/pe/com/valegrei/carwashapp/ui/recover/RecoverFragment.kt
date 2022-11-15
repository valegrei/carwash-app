package pe.com.valegrei.carwashapp.ui.recover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.FragmentRecoverBinding

class RecoverFragment : Fragment() {
    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Configura Toolbar con Navigation Component
        binding.apply {
            recoverFragment = this@RecoverFragment
        }
    }

    fun goNewPass() {
        findNavController().navigate(R.id.action_recoverFragment_to_newPasswordFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}