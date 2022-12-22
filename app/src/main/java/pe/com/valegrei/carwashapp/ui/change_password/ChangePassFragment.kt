package pe.com.valegrei.carwashapp.ui.change_password

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentChangePassBinding
import pe.com.valegrei.carwashapp.ui.util.ProgressDialog

class ChangePassFragment : Fragment() {

    private var _binding: FragmentChangePassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChangePassViewModel by viewModels {
        ChangePassViewModelFactory(SesionData(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangePassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@ChangePassFragment
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ChangePassFragment.viewModel
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> showLoading()
                Status.ERROR -> hideLoading()
                Status.SUCCESS -> exit()
                else -> {}
            }
        }
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

    fun exit() {
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
            findNavController().popBackStack()
        }, 500)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}