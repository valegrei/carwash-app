package pe.com.valegrei.carwashapp.ui.verify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.VerifyFragmentArgs
import pe.com.valegrei.carwashapp.databinding.FragmentVerifyBinding

class VerifyFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private val verifyViewModel: VerifyViewModel by viewModels {
        VerifyViewModelFactory(
            (activity?.application as CarwashApplication).database.sesionDao(),
            (activity?.application as CarwashApplication).database.usuarioDao()
        )
    }

    private val args: VerifyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Carga datos
        verifyViewModel.idUsuario.value = args.idUsuario
        verifyViewModel.correo.value = args.correo

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun verificar(){
        verifyViewModel.verificar()
    }

    fun enviarCodigo(){
        verifyViewModel.enviarCodigo()
    }

}