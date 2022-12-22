package pe.com.valegrei.carwashapp.ui.distribs

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentDistribsBinding

class DistribsFragment : Fragment(), MenuProvider, OnQueryTextListener {

    private var _binding: FragmentDistribsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DistribsViewModel by activityViewModels {
        DistribsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.usuarioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistribsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val distribAdapter = DistribsListAdapter {
            viewModel.setSelectedDistrib(it)
            DistribsBottomSheedDialog().show(childFragmentManager, DistribsBottomSheedDialog.TAG)
        }

        binding.rvDistrib.adapter = distribAdapter

        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarDistribuidores().collect() {
                distribAdapter.submitList(it)
            }
        }

        binding.swipeDistrib.setColorSchemeResources(R.color.purple)

        binding.swipeDistrib.setOnRefreshListener {
            viewModel.descargarDistribuidores()
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeDistrib.isRefreshing = true
                else -> binding.swipeDistrib.isRefreshing = false
            }
        }
        viewModel.goStatus.observe(viewLifecycleOwner) {
            when (it) {
                GoStatus.SHOW_CONFIRM -> showConfirmar()
                GoStatus.SHOW_DENEG -> showDenegar()
                else -> {}
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.descargarDistribuidores()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.action_search)
        searchView.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        (binding.rvDistrib.adapter as DistribsListAdapter).filter.filter(newText)
        return false
    }

    private fun showConfirmar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.btnsh_aprove)
            .setMessage(R.string.btnsh_aprove_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.aprobarDist(true)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
        viewModel.clearGoStatus()
    }

    private fun showDenegar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.btnsh_reject)
            .setMessage(R.string.btnsh_reject_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.aprobarDist(false)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
        viewModel.clearGoStatus()
    }

}