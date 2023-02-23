package pe.com.carwashperuapp.carwashapp.ui.menu_distrib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.MainViewModel
import pe.com.carwashperuapp.carwashapp.MainViewModelFactory
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentMenuDistribBinding
import pe.com.carwashperuapp.carwashapp.ui.bindImageBanner

class MenuDistribFragment : Fragment() {

    private var _binding: FragmentMenuDistribBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            SesionData(requireContext()),
            appDataBase = (requireActivity().application as CarwashApplication).database
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMenuDistribBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragment = this
        binding.viewModel = mainViewModel
        (activity as AppCompatActivity).supportActionBar?.title =
            mainViewModel.getTipoPerfilNombre()

        mainViewModel.sesion.observe(viewLifecycleOwner) {
            if (mainViewModel.mostrarBanner())
                bindImageBanner(
                    requireActivity().findViewById(R.id.img_banner),
                    it.usuario.getURLFoto()
                )
        }
    }

    fun goToAccount() {
        findNavController().navigate(R.id.action_navigation_menu_to_navigation_account)
    }

    fun goToChangePass() {
        findNavController().navigate(R.id.action_navigation_menu_to_navigation_change_pass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}