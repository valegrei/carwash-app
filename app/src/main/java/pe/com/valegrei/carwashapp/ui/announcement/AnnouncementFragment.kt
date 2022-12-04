package pe.com.valegrei.carwashapp.ui.announcement

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pe.com.valegrei.carwashapp.R

class AnnouncementFragment : Fragment() {

    companion object {
        fun newInstance() = AnnouncementFragment()
    }

    private lateinit var viewModel: AnnouncementViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_announcement, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AnnouncementViewModel::class.java)
        // TODO: Use the ViewModel
    }

}