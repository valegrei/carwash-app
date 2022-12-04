package pe.com.valegrei.carwashapp.ui.distribs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentDistribsBinding

class DistribsFragment : Fragment() {

    private var _binding: FragmentDistribsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DistribsViewModel by viewModels {
        DistribsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.usuarioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDistribsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val distribAdapter =DistribsListAdapter{
            //TODO implementar interaccion
        }

        binding.rvDistrib.adapter = distribAdapter

        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarDistribuidores().collect(){
                distribAdapter.submitList(it)
            }
        }

        binding.swipeDistrib.setColorSchemeResources(R.color.purple)

        binding.swipeDistrib.setOnRefreshListener {
            viewModel.descargarDistribuidores()
        }

        viewModel.status.observe(viewLifecycleOwner){
            when(it){
                Status.LOADING -> binding.swipeDistrib.isRefreshing = true
                else -> binding.swipeDistrib.isRefreshing = false
            }
        }

        viewModel.descargarDistribuidores()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}