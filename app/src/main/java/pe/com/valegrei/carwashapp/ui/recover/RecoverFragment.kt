package pe.com.valegrei.carwashapp.ui.recover

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.databinding.FragmentRecoverBinding
import pe.com.valegrei.carwashapp.ui.util.ProgressDialog

class RecoverFragment : Fragment() {
    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding!!
    private val recoverViewModel: RecoverViewModel by viewModels()

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
            lifecycleOwner = viewLifecycleOwner
            viewModel = recoverViewModel
        }

        recoverViewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> showLoading()
                Status.ERROR -> hideLoading()
                Status.SUCCESS -> goNewPass()
                else -> {}
            }
        }
    }

    fun goNewPass() {
        Handler(Looper.getMainLooper()).postDelayed({
            recoverViewModel.clear()
            progressDialog.dismiss()
            val action = RecoverFragmentDirections
                .actionRecoverFragmentToNewPasswordFragment(recoverViewModel.correo.value!!)
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