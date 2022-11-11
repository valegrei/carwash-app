package pe.com.valegrei.carwashapp.ui.reserve_list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import pe.com.valegrei.carwashapp.R
import pe.com.valegrei.carwashapp.databinding.FragmentReserveListBinding
import pe.com.valegrei.carwashapp.model.Reserva
import java.text.SimpleDateFormat
import java.util.*

class ReserveListFragment : Fragment(), MenuProvider, ReserveListAdapter.OnInteractionListener {

    private var _binding: FragmentReserveListBinding? = null
    private val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
            ViewModelProvider(this)[ReserveListViewModel::class.java]

        reserveListViewModel.reserveList.observe(viewLifecycleOwner) {
            binding.rvReserveList.adapter = ReserveListAdapter(it, this)
        }

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onResume() {
        super.onResume()
        //fecha de hoy
        setSubTitle(format.format(Date()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_search) {
            showDatePicker()
            return true
        }
        return false
    }

    //Muestra el selector de fechas
    private fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccion fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            val fecha = Date(it)
            setSubTitle(format.format(fecha))
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun setSubTitle(subTitle: String) {
        (activity as AppCompatActivity).supportActionBar?.subtitle = subTitle
    }

    override fun onClick(item: Reserva) {
        findNavController().navigate(R.id.action_nav_reserve_list_to_serviceDetailsFragment)
    }
}