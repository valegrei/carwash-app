package pe.com.carwashperuapp.carwashapp.ui.announcement_cli

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.database.anuncio.Anuncio
import pe.com.carwashperuapp.carwashapp.databinding.DialogAnuncioBinding

class AnuncioDialog : DialogFragment() {
    private var _binding: DialogAnuncioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnunciosViewModel by activityViewModels {
        AnunciosViewModelFactory((requireActivity().application as CarwashApplication).database.anuncioDao())
    }
    private var anuncio: Anuncio? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = DialogAnuncioBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anuncio = viewModel.anuncios.value?.get(0)!!
        binding.apply {
            anuncio = this@AnuncioDialog.anuncio
            fragment = this@AnuncioDialog
        }
    }

    fun cerrar() {
        dismiss()
    }

    fun goUrl() {
        if (!anuncio?.url.isNullOrEmpty()) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(anuncio?.url)
            startActivity(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}