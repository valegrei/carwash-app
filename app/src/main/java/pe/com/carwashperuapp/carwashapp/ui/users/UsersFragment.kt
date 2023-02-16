package pe.com.carwashperuapp.carwashapp.ui.users

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentUsersBinding

class UsersFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var job: Job
    private lateinit var usuariosAdapter: UsersListAdapter

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

        usuariosAdapter = UsersListAdapter {
            viewModel.setSelectedUsu(it)
            UsersBottomSheedDialog().show(childFragmentManager, UsersBottomSheedDialog.TAG)
        }

        binding.rvUsers.adapter = usuariosAdapter

        //Actualiza la vista en tiempo real
        cargarUsuarios()

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
                GoStatus.SHOW_CHANGE_PASSWORD -> showChangePassword()
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

    private fun cargarUsuarios() {
        job = lifecycle.coroutineScope.launch {
            viewModel.cargarUsuarios().cancellable().collect() {
                usuariosAdapter.submitList(it)
                onQueryTextChange(searchView?.query?.toString())
            }
        }
    }

    private fun cancelarCargaUsuarios(){
        job.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var searchView: SearchView?=null
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_usu_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.action_search)
        searchView?.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.action_add_admin -> {
                findNavController().navigate(R.id.action_navigation_users_to_addAdminFragment)
                return true
            }
            R.id.action_filter_by -> {
                mostrarFiltros()
                return true
            }
        }
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (isVisible)
            (binding.rvUsers.adapter as UsersListAdapter).filter.filter(newText)
        return true
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

    private fun showChangePassword() {
        findNavController().navigate(R.id.action_navigation_users_to_usersChangePassFragment)
        viewModel.clearGoStatus()
    }

    fun mostrarFiltros() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(R.string.users_filter_by)
        val items = arrayOf(
            getString(R.string.action_show_admin),
            getString(R.string.action_show_cli),
            getString(R.string.action_show_dis),
        )
        val checkedItems = booleanArrayOf(
            viewModel.showAdmin.value ?: false,
            viewModel.showCli.value ?: false,
            viewModel.showDis.value ?: false,
        )
        alertDialog.setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }
        alertDialog.setPositiveButton(R.string.accept) { _, _ ->
            viewModel.setFiltros(checkedItems)
            volverCargarUsuarios()
        }
        alertDialog.setNegativeButton(R.string.cancel, null)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun volverCargarUsuarios(){
        cancelarCargaUsuarios()
        cargarUsuarios()
    }

}