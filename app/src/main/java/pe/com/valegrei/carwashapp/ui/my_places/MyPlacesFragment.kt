package pe.com.valegrei.carwashapp.ui.my_places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pe.com.valegrei.carwashapp.databinding.FragmentMyPlacesBinding

class MyPlacesFragment : Fragment() {

    private var _binding: FragmentMyPlacesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myPlacesViewModel =
            ViewModelProvider(this).get(MyPlacesViewModel::class.java)

        _binding = FragmentMyPlacesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        myPlacesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}