package pe.com.carwashperuapp.carwashapp.ui.reserve

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import pe.com.carwashperuapp.carwashapp.CarwashApplication
import pe.com.carwashperuapp.carwashapp.R
import pe.com.carwashperuapp.carwashapp.database.SesionData
import pe.com.carwashperuapp.carwashapp.databinding.FragmentSearchLocalBinding
import pe.com.carwashperuapp.carwashapp.model.Local
import pe.com.carwashperuapp.carwashapp.ui.my_places.PlaceAutcompleteListAdapter

class SearchLocalFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener {
    companion object {
        val TAG: String = SearchLocalFragment::class.java.name
    }

    private var _binding: FragmentSearchLocalBinding? = null
    private val binding get() = _binding!!

    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    lateinit var placesClient: PlacesClient
    lateinit var adapter: PlaceAutcompleteListAdapter

    // Current location is set to Lima, this will be of no use
    var currentLocation: LatLng = LatLng(-12.046664, -77.0431219)

    private val viewModel: ReserveViewModel by activityViewModels {
        ReserveViewModelFactory(
            SesionData(requireContext()),
            (activity?.application as CarwashApplication).database.direccionDao(),
            (activity?.application as CarwashApplication).database.vehiculoDao(),
        )
    }

    // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
    // and once again when the user makes a selection (for example when calling fetchPlace()).
    private var token: AutocompleteSessionToken? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.setOnMarkerClickListener {
            viewModel.selectLocal(it, currentLocation)
            LocalBottomSheedDialog().show(childFragmentManager, LocalBottomSheedDialog.TAG)
            true
        }
        mMap.setOnCameraIdleListener {
            searchLocales()
        }
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12.5F))
        getLastLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchLocalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiKey = getString(R.string.api_key)

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, apiKey)
        }
        // Instancio placesCliente
        placesClient = Places.createClient(requireContext())

        // Initializing Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Adding functionality to the button
        binding.btnCenter.setOnClickListener {
            getLastLocation()
        }
//        binding.btnSearchArea.setOnClickListener {
//            searchLocales()
//        }

        viewModel.locales.observe(viewLifecycleOwner) {
            mostrarLocales(it)
        }

        viewModel.goStatus.observe(viewLifecycleOwner){
            when(it){
                GoStatus.GO_ADD -> goNuevaReserva()
                else -> {}
            }
        }

        //Recyclerview
        adapter = PlaceAutcompleteListAdapter { goPlace(it.placeId) }
        binding.rvPlacesList.adapter = adapter

        //Configura el menu del fragment
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

    }

    private fun goNuevaReserva() {
        findNavController().navigate(R.id.action_nav_reserve_to_reserveFragment)
        viewModel.clearGoStatus()
    }

    private fun mostrarLocales(locales: List<Local>) {
        val map = mutableMapOf<Marker, Local>()
        mMap.clear()
        locales.forEach {
            val markerOp = MarkerOptions()
                .position(LatLng(it.latitud.toDouble(), it.longitud.toDouble()))
                .title(it.distrib?.razonSocial)
            val marker = mMap.addMarker(markerOp)
            map[marker!!] = it
        }
        viewModel.setMarkersData(map)
    }

    // Get current location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        //mMap.clear()
                        //mMap.addMarker(MarkerOptions().position(currentLocation))
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
                                12.5F
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    // Get current location, if shifted
    // from previous location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMaxUpdates(1)
            setMinUpdateIntervalMillis(0)
        }.build()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private var searchView: SearchView? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.action_search)
        searchView?.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        buscar(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        buscar(newText)
        return true
    }

    private fun buscar(query: String?) {
        if (query?.trim()!!.isNotEmpty()) {
            if (token == null)
                token = AutocompleteSessionToken.newInstance()
            showPlaceList()
            autocompletar(query)
        } else {
            showMap()
        }
    }

    private fun autocompletar(query: String) {

        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setOrigin(currentLocation)
                .setCountries("PE")
                //.setTypesFilter(listOf(TypeFilter.ADDRESS.toString()))
                .setSessionToken(token)
                .setQuery(query)
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                adapter.submitList(
                    response.autocompletePredictions.sortedBy { it.distanceMeters ?: 1000000 }
                )
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.message)
                }
            }
    }

    private fun showMap() {
        if (!binding.lyMap.isVisible) {
            binding.lyMap.visibility = View.VISIBLE
        }
        if (binding.rvPlacesList.isVisible) {
            binding.rvPlacesList.visibility = View.GONE
        }
        //ocultar teclado
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun showPlaceList() {
        if (binding.lyMap.isVisible) {
            binding.lyMap.visibility = View.GONE
        }
        if (!binding.rvPlacesList.isVisible) {
            binding.rvPlacesList.visibility = View.VISIBLE
        }
    }

    private fun goPlace(placeId: String) {
        // Specify the fields to return.
        val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)

        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                showMap()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(place.latLng!!, 12.5F),
                )
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.message}")
                }
            }
    }

    private fun searchLocales() {
        val bounds = mMap.projection.visibleRegion.latLngBounds
        val cornerNE = bounds.northeast
        val cornerSW = bounds.southwest
        viewModel.consultarLocales(cornerNE, cornerSW)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}