package pe.com.valegrei.carwashapp.ui.announcement

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.launch
import pe.com.valegrei.carwashapp.CarwashApplication
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.database.SesionData
import pe.com.valegrei.carwashapp.databinding.FragmentAnnouncementBinding

class AnnouncementFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAnnouncementBinding? = null
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
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val anunciosAdapter = AnnouncementGridAdapter {
            viewModel.verAnuncio(it)
            findNavController().navigate(R.id.action_navigation_announcement_to_navigation_announcement_new)
        }

        binding.rvAnuncios.adapter = anunciosAdapter

        //Actualiza la vista en tiempo real
        lifecycle.coroutineScope.launch {
            viewModel.cargarAnuncios().collect() {
                anunciosAdapter.submitList(it)
            }
        }

        binding.swipeAnuncios.setColorSchemeResources(R.color.purple)
        binding.swipeAnuncios.setOnRefreshListener {
            viewModel.descargarAnuncios()
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                Status.LOADING -> binding.swipeAnuncios.isRefreshing = true
                else -> binding.swipeAnuncios.isRefreshing = false
            }
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        viewModel.descargarAnuncios()
    }

    fun mostrarEscogerImagen() {
        customCropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = false,
                    imageSourceIncludeGallery = true,
                    outputCompressFormat = Bitmap.CompressFormat.JPEG,
                    outputCompressQuality = 90,
                    skipEditing = true,
                    activityTitle = getString(R.string.announc_title_new),
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
        viewModel.nuevoAnuncio(uri, filePath)
        findNavController().navigate(R.id.action_navigation_announcement_to_navigation_announcement_new)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_add) {
            mostrarEscogerImagen()
            return true
        }
        return false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}