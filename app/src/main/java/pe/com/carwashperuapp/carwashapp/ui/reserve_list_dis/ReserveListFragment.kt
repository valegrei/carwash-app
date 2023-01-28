package pe.com.carwashperuapp.carwashapp.ui.reserve_list_dis

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.databinding.FragmentReserveListBinding
import pe.com.carwashperuapp.carwashapp.model.Reserva1
import java.text.SimpleDateFormat
import java.util.*

class ReserveListFragment : Fragment(), MenuProvider {

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

        //Adapter de RV
        reserveListViewModel.reserveList.observe(viewLifecycleOwner) {
            binding.rvReserveList.adapter = ReserveListAdapter(it){r->
                goReserva(r)
            }
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
        menuInflater.inflate(R.menu.search_date, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_search_date) {
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

    fun goReserva(item: Reserva1) {
        findNavController().navigate(R.id.action_nav_reserve_list_to_serviceDetailsFragment)
    }
}