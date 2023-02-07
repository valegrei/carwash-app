package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentLocalDetalleBinding

class LocalDetalleFragment : Fragment() {

    private var _binding: FragmentLocalDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@LocalDetalleFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    fun showMap() {
        val direccion = viewModel.selectedLocal.value
        // Display a label at the location of Google's Sydney office
        val gmmIntentUri =
            Uri.parse("geo:${direccion?.latitud},${direccion?.longitud}?z=16&q=${direccion?.latitud},${direccion?.longitud}(${direccion?.direccion})")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}