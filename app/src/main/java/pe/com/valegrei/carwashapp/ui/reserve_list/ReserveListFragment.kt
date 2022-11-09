package pe.com.valegrei.carwashapp.ui.reserve_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pe.com.valegrei.carwashapp.databinding.FragmentReserveListBinding

class ReserveListFragment : Fragment() {

    private var _binding: FragmentReserveListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reserveListViewModel =
            ViewModelProvider(this).get(ReserveListViewModel::class.java)

        reserveListViewModel.reserveList.observe(viewLifecycleOwner) {
            binding.rvReserveList.adapter = ReserveListAdapter(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}