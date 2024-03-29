package pe.com.carwashperuapp.carwashapp.ui.announcement

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.*
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentAnnouncementNewBinding

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
            EditStatus.VIEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.announc_title_view)
            EditStatus.EDIT -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.announc_title_edit)
            EditStatus.NEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.announc_title_new)
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
                showEliminar()
                true
            }
            R.id.action_edit -> {
                viewModel.editarAnuncio()
                true
            }
            R.id.action_save -> {
                showGuardar()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEliminar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.announc_delete)
            .setMessage(R.string.announc_delete_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.eliminarAnuncio()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showGuardar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.announc_save)
            .setMessage(R.string.announc_save_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.guardarAnuncio()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }


    fun mostrarEscogerImagen() {
        if (viewModel.editStatus.value == EditStatus.EDIT || viewModel.editStatus.value == EditStatus.NEW) {
            customCropImage.launch(
                CropImageContractOptions(
                    uri = null,
                    cropImageOptions = CropImageOptions(
                        imageSourceIncludeCamera = false,
                        imageSourceIncludeGallery = true,
                        outputCompressFormat = Bitmap.CompressFormat.JPEG,
                        outputCompressQuality = 98,
                        outputRequestWidth = 1000,
                        outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_FIT,
                        skipEditing = true,
                        activityTitle = getString(R.string.announc_title_new),
                        activityBackgroundColor = resources.getColor(R.color.bg_color, null),
                        toolbarColor = resources.getColor(R.color.purple, null),
                        toolbarBackButtonColor = Color.WHITE,
                        toolbarTitleColor = Color.WHITE,
                        toolbarTintColor = Color.WHITE,
                    ),
                ),
            )
        }
    }

    private val customCropImage = registerForActivityResult(CropImageContract()) {
        if (it !is CropImage.CancelledResult) {
            handleCropImageResult(it.uriContent, it.getUriFilePath(requireContext()))
        }
    }

    private fun handleCropImageResult(uri: Uri?, filePath: String?) {
        viewModel.cambiarImagen(uri, filePath)
    }
}