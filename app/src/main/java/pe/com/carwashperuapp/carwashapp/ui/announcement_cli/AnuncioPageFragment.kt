package pe.com.carwashperuapp.carwashapp.ui.announcement_cli

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pe.com.carwashperuapp.carwashapp.databinding.FragmentAnuncioPageBinding
import pe.com.carwashperuapp.carwashapp.network.BASE_URL

private const val ARG_PATH_FOTO_URL = "pathFotoUrl"
private const val ARG_URL = "url"

class AnuncioPageFragment : Fragment() {
    private var pathFotoUrl: String? = null
    private var url: String? = null

    private var _binding: FragmentAnuncioPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pathFotoUrl = it.getString(ARG_PATH_FOTO_URL)
            url = it.getString(ARG_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnuncioPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fotoUrl = "$BASE_URL$pathFotoUrl"
        binding.fotoUrl = fotoUrl
        binding.fragment = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goUrl() {
        if (!url.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(pathFotoUrl: String, url: String?) =
            AnuncioPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PATH_FOTO_URL, pathFotoUrl)
                    putString(ARG_URL, url)
                }
            }
    }
}