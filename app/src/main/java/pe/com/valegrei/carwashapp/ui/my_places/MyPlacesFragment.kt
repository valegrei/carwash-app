package pe.com.valegrei.carwashapp.ui.my_places

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.FragmentMyPlacesBinding
import pe.com.valegrei.carwashapp.model.Local
import pe.com.valegrei.carwashapp.ui.reserve_list.ReserveListAdapter
import pe.com.valegrei.carwashapp.ui.reserve_list.ReserveListViewModel

class MyPlacesFragment : Fragment(), MenuProvider , MyPlacesListAdapter.OnInteractionListener{

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myPlacesViewModel =
            ViewModelProvider(this)[MyPlacesViewModel::class.java]
        //Adapter de RV
        myPlacesViewModel.localList.observe(viewLifecycleOwner) {
            binding.rvPlacesList.adapter = MyPlacesListAdapter(it, this)
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_place, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_add_place) {
            findNavController().navigate(R.id.action_nav_my_places_to_addPlaceFragment)
            return true
        }
        return false
    }

    override fun onClick(item: Local) {
        findNavController().navigate(R.id.action_nav_my_places_to_addPlaceFragment)
    }
}