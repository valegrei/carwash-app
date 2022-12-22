package pe.com.valegrei.carwashapp.ui.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentUsersBinding

class UsersFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener {

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
    ): View {
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
        viewModel.goStatus.observe(viewLifecycleOwner) {
            when (it) {
                GoStatus.SHOW_DELETE -> showEliminar()
                else -> {}
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.descargarUsuarios()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_usu_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.action_search)
        searchView.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_add_admin) {
            findNavController().navigate(R.id.action_navigation_users_to_addAdminFragment)
            return true
        }
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        (binding.rvUsers.adapter as UsersListAdapter).filter.filter(newText)
        return false
    }

    private fun showEliminar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.btnsh_delete)
            .setMessage(R.string.btnsh_delete_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.eliminarUsuario()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
        viewModel.clearGoStatus()
    }

}