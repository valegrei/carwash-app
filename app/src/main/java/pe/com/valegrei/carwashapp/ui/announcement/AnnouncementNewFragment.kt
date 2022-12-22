package pe.com.valegrei.carwashapp.ui.announcement

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentAnnouncementNewBinding

class AnnouncementNewFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAnnouncementNewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnnouncementViewModel by activityViewModels {
        AnnouncementViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.anuncioDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnnouncementNewBinding.inflate(inflater, container, false)

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@AnnouncementNewFragment.viewModel
            fragment = this@AnnouncementNewFragment
        }

        viewModel.editStatus.observe(viewLifecycleOwner) {
            mostrarBotones(it)
            mostrarTitulo(it)
            salir(it)
        }
    }

    fun mostrarTitulo(status: EditStatus) {
        when (status) {
            EditStatus.VIEW -> (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.announc_title_view)
            EditStatus.EDIT -> (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.announc_title_edit)
            EditStatus.NEW -> (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.announc_title_new)
            else -> {}
        }
    }

    fun mostrarBotones(status: EditStatus) {
        when (status) {
            EditStatus.VIEW -> {
                deleteItem?.setVisible(true)
                editItem?.setVisible(true)
                saveItem?.setVisible(false)
            }
            EditStatus.EDIT, EditStatus.NEW -> {
                deleteItem?.setVisible(false)
                editItem?.setVisible(false)
                saveItem?.setVisible(true)
            }
            else -> {}
        }
    }

    fun salir(editStatus: EditStatus) {
        if (editStatus == EditStatus.EXIT)
            findNavController().popBackStack()
    }

    private var deleteItem: MenuItem? = null
    private var editItem: MenuItem? = null
    private var saveItem: MenuItem? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_item_menu, menu)
        deleteItem = menu.findItem(R.id.action_delete)
        editItem = menu.findItem(R.id.action_edit)
        saveItem = menu.findItem(R.id.action_save)
        mostrarBotones(viewModel.editStatus.value!!)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                viewModel.eliminarAnuncio()
                true
            }
            R.id.action_edit -> {
                viewModel.editarAnuncio()
                true
            }
            R.id.action_save -> {
                viewModel.guardarAnuncio()
                true
            }
            else -> false
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}