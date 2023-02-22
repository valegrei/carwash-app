package pe.com.carwashperuapp.carwashapp.ui.my_cars

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
import pe.com.carwashperuapp.carwashapp.databinding.FragmentCarDetailBinding
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class CarDetailFragment : Fragment(), MenuProvider {

    private var _binding: FragmentCarDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MyCarsViewModel by activityViewModels {
        MyCarsViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@CarDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            fragment = this@CarDetailFragment
        }

        viewModel.apply {
            editStatus.observe(viewLifecycleOwner) {
                mostrarTitulo(it)
                mostrarBotones(it)
                salir(it)
            }
            status.observe(viewLifecycleOwner) {
                when (it) {
                    Status.LOADING -> showLoading()
                    Status.ERROR, Status.SUCCESS -> hideLoading()
                    else -> {}
                }
            }
            errMarca.observe(viewLifecycleOwner) {
                binding.tiMarca.error = it
            }
            errModelo.observe(viewLifecycleOwner) {
                binding.tiModelo.error = it
            }
            errYear.observe(viewLifecycleOwner) {
                binding.tiYear.error = it
            }
            errPlaca.observe(viewLifecycleOwner) {
                binding.tiPlaca.error = it
            }
            addFotoStatus.observe(viewLifecycleOwner) {
                when (it) {
                    AddFotoStatus.LAUNCH -> {
                        startCameraWithoutUri()
                        ocultarAddFoto()
                    }
                    else -> {}
                }
            }
        }


        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    fun mostrarEditarFotoOpc() {
        if (viewModel.mostrarEditar.value!!)
            EditPhotoBottomSheetDialog().show(childFragmentManager, EditPhotoBottomSheetDialog.TAG)
    }

    private fun salir(editStatus: EditStatus) {
        if (editStatus == EditStatus.EXIT)
            findNavController().popBackStack()
    }

    fun mostrarTitulo(status: EditStatus) {
        when (status) {
            EditStatus.VIEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.car_title_view)
            EditStatus.EDIT -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.car_title_edit)
            EditStatus.NEW -> (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.car_title_new)
            else -> {}
        }
    }

    private fun startCameraWithoutUri() {
        customCropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = true,
                    imageSourceIncludeGallery = true,
                    cropShape = CropImageView.CropShape.RECTANGLE,
                    fixAspectRatio = true,
                    aspectRatioX = 2,
                    aspectRatioY = 1,
                    outputCompressFormat = Bitmap.CompressFormat.JPEG,
                    outputCompressQuality = 98,
                    outputRequestWidth = 1024,
                    outputRequestHeight = 512,
                    outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_FIT,
                    showIntentChooser = true,
                    intentChooserTitle = "Seleccionar fuente",
                    autoZoomEnabled = false,
                    multiTouchEnabled = true,
                    centerMoveEnabled = true,
                    cropMenuCropButtonIcon = R.drawable.ic_baseline_check_24,
                    activityTitle = "Cambiar foto",
                    activityBackgroundColor = Color.DKGRAY,
                    toolbarColor = resources.getColor(R.color.purple, null),
                    toolbarBackButtonColor = Color.WHITE,
                    toolbarTitleColor = Color.WHITE,
                    toolbarTintColor = Color.WHITE
                ),
            ),
        )
    }

    private val customCropImage = registerForActivityResult(CropImageContract()) {
        if (it !is CropImage.CancelledResult) {
            handleCropImageResult(it.uriContent, it.getUriFilePath(requireContext()))
        }
    }

    private fun handleCropImageResult(uri: Uri?, filePath: String?) {
        viewModel.nuevaFoto(uri, filePath)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    fun mostrarBotones(status: EditStatus) {
        when (status) {
            EditStatus.VIEW -> {
                deleteItem?.isVisible = true
                editItem?.isVisible = true
                saveItem?.isVisible = false
            }
            EditStatus.EDIT, EditStatus.NEW -> {
                deleteItem?.isVisible = false
                editItem?.isVisible = false
                saveItem?.isVisible = true
            }
            else -> {}
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                showEliminar()
                true
            }
            R.id.action_edit -> {
                viewModel.editarVehiculo()
                true
            }
            R.id.action_save -> {
                showGuardar()
                true
            }
            else -> false
        }
    }


    private fun showEliminar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.car_delete)
            .setMessage(R.string.car_delete_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.eliminarVehiculo()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showGuardar() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.car_save)
            .setMessage(R.string.car_save_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.accept) { _, _ ->
                viewModel.guardarVehiculo()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    var progressDialog = ProgressDialog()

    fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    fun hideLoading() {
        if (progressDialog.isVisible)
            progressDialog.dismiss()
    }
}