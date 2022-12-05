package pe.com.valegrei.carwashapp.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.database.usuario.Usuario
import pe.com.valegrei.carwashapp.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsersViewModel by activityViewModels {
        UsersViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.usuarioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuariosAdapter = UsersListAdapter {
            viewModel.setSelectedUsu(it)
            UsersBottomSheedDialog().show(childFragmentManager, UsersBottomSheedDialog.TAG)
        }

        binding.rvUsers.adapter = usuariosAdapter

        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarUsuarios().collect() {
                usuariosAdapter.submitList(it)
            }
        }

        binding.swipeUsers.setColorSchemeResources(R.color.purple)
        binding.swipeUsers.setOnRefreshListener {
            viewModel.descargarUsuarios()
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeUsers.isRefreshing = true
                else -> binding.swipeUsers.isRefreshing = false
            }
        }

        viewModel.descargarUsuarios()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}