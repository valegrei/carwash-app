package pe.com.carwashperuapp.carwashapp.ui.account

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.*
import pe.com.carwashperuapp.carwashapp.*
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.database.usuario.TipoDocumento
import pe.com.carwashperuapp.carwashapp.databinding.FragmentAccountEditBinding
import pe.com.carwashperuapp.carwashapp.ui.bindImageEdit
import pe.com.carwashperuapp.carwashapp.ui.util.ProgressDialog

class AccountEditFragment : Fragment(), MenuProvider {
    companion object {
        val TAG = "AccountEditFragment"
    }

    private var _binding: FragmentAccountEditBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(SesionData(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            accountEditFragment = this@AccountEditFragment
        }
        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
        binding.acTipoDoc.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedTipoDoc = adapterView.getItemAtPosition(position) as TipoDocumento
            sharedViewModel.setSelectedTipoDoc(selectedTipoDoc)
        }

        sharedViewModel.apply {
            status.observe(viewLifecycleOwner) {
                when (it) {
                    EditStatus.LOADING -> showLoading()
                    EditStatus.ERROR -> hideLoading()
                    EditStatus.SUCCESS -> exit()
                    else -> {}
                }
            }
            tiposDoc.observe(viewLifecycleOwner) {
                binding.acTipoDoc.setText(selectedTipoDoc.value?.toString())
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
                binding.acTipoDoc.setAdapter(adapter)
            }
            errNombres.observe(viewLifecycleOwner) {
                binding.tiNombres.error = it
            }
            errApePat.observe(viewLifecycleOwner) {
                binding.tiApePat.error = it
            }
            errApeMat.observe(viewLifecycleOwner) {
                binding.tiApeMat.error = it
            }
            errRazSoc.observe(viewLifecycleOwner) {
                binding.tiRazSoc.error = it
            }
            errNroDoc.observe(viewLifecycleOwner) {
                binding.tiNroDoc.error = it
            }
            errNroTelef.observe(viewLifecycleOwner) {
                binding.tiNroTelef.error = it
            }
            errNroWhatsapp.observe(viewLifecycleOwner) {
                binding.tiNroWhtasapp.error = it
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
            ternaFoto.observe(viewLifecycleOwner) {
                if (sharedViewModel.mostrarBanner())
                    bindImageEdit(requireActivity().findViewById(R.id.img_banner), it)
            }
        }

    }

    private fun startCameraWithoutUri() {
        customCropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = false,
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
                    showIntentChooser = false,
                    autoZoomEnabled = false,
                    multiTouchEnabled = true,
                    centerMoveEnabled = true,
                    cropMenuCropButtonIcon = R.drawable.ic_baseline_check_24,
                    activityTitle = getString(R.string.my_data_change_photo),
                    activityBackgroundColor = resources.getColor(R.color.bg_color, null),
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
        sharedViewModel.nuevaFoto(uri, filePath)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.acc_dist_menu, menu)
        if (!sharedViewModel.mostrarBanner()) {
            val menuBanner = menu.findItem(R.id.action_add_banner)
            menuBanner.isVisible = false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_save) {
            sharedViewModel.guardarCambios()
        } else if (menuItem.itemId == R.id.action_add_banner) {
            mostrarEditarFotoOpc()
        }
        return false
    }

    fun mostrarEditarFotoOpc() {
        EditPhotoBannerBottomSheetDialog().show(
            childFragmentManager,
            EditPhotoBannerBottomSheetDialog.TAG
        )
    }

    var progressDialog = ProgressDialog()

    fun showLoading() {
        progressDialog.show(childFragmentManager, "progressDialog")
    }

    fun hideLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
        }, 500)
    }

    fun exit() {
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
            findNavController().popBackStack()
        }, 500)
    }


}