package pe.com.carwashperuapp.carwashapp.ui.announcement_cli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.databinding.FragmentAnunciosBinding

class AnunciosFragment : Fragment() {
    private var _binding: FragmentAnunciosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnunciosViewModel by activityViewModels {
        AnunciosViewModelFactory((requireActivity().application as CarwashApplication).database.anuncioDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnunciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.announcesViewpager.adapter = AnunciosAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.announcesViewpager) { _, _ -> }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("DEPRECATION")
    private inner class AnunciosAdapter(fr: Fragment) : FragmentStateAdapter(fr) {
        override fun getItemCount(): Int {
            return viewModel.anuncios.value?.size ?: 0
        }

        override fun createFragment(position: Int): Fragment {
            val anuncio = viewModel.anuncios.value?.get(position)!!
            return AnuncioPageFragment.newInstance(anuncio.path, anuncio.url)
        }

    }
}