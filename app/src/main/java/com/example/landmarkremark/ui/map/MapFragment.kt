package com.example.landmarkremark.ui.map

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.landmarkremark.MainActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.databinding.FragmentMapBinding
import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.ui.bottomsheet.markerinfo.MarkerInfoSheetFragment
import com.example.landmarkremark.ui.bottomsheet.markernew.MarkerNewSheetFragment
import com.example.landmarkremark.utils.MyDateTime
import com.example.landmarkremark.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var myLocationOverlay: MyLocationNewOverlay? = null
    private lateinit var mapController: IMapController
    private var currenLocation: GeoPoint? = null

    private var tmpMarker: Marker? = null
    private var isMarkerSaved: Boolean = false

    private var userName: String = ""

    private lateinit var adapter: ArrayAdapter<Notes>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView

        userName = (activity as? MainActivity)?.getUserName() ?: ""

        //setup config for Open Street Map
        initOSMConfiguration()
        observerState()
        loadNotesData()

        //broadcast receiver location change
//        val br: BroadcastReceiver = LocationChangedProvider()
//        val filter =
//            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
//
//        activity?.registerReceiver(br, filter)

//        mfusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(
//                requireActivity()
//            )

        return binding.root
    }

    private fun loadNotesData() {
        homeViewModel.getAllNotes()
    }

    private val requestPermision =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d("PERMISSION", isGranted.toString())

            if (isGranted) {
                Log.d("PERMISSION", isGranted.toString())
                currentLocationUpdate()
            } else {
                requestLocationPermissions()
            }
        }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        var lastTapTime: Long = 0
        val debounceThreshold = 500

        mapView.overlays.add(MapEventsOverlay(object :
            MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let { gPoint ->

                    //avoid double pin location on map
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTapTime < debounceThreshold) return true
                    lastTapTime = currentTime

                    addMarkerOnMap(gPoint)
                }

                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }

        }))


        binding.btnMyLocation.setOnClickListener {

            if (!isLocationPermissionGranted()) {
                requestPermision.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                currentLocationUpdate()
            }
        }

        binding.autoCompleteTextView.addTextChangedListener {
            val value = it.toString()

            if (value.length > 1) {
                homeViewModel.searchNoteByUsername(value)
            }
        }

        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            binding.autoCompleteTextView.text.clear()

            val noteSelected =
                parent.getItemAtPosition(position) as Notes

            val geoPoint = GeoPoint(
                noteSelected.latitude,
                noteSelected.longitude
            )


            mapController.animateTo(geoPoint, 10.0, 600L)
            showMarkerInfoSheet(noteSelected)
            Log.d("ITEM_SELECTED", noteSelected.toString())

        }

        binding.autoCompleteTextView.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    binding.autoCompleteTextView.setText("")
                }
            }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun currentLocationUpdate() {

        val locationProvider = GpsMyLocationProvider(requireContext())

        myLocationOverlay = MyLocationNewOverlay(
            locationProvider, mapView
        )

        myLocationOverlay?.enableMyLocation()
//        myLocationOverlay.enableFollowLocation()
        myLocationOverlay?.isDrawAccuracyEnabled = true

        myLocationOverlay?.runOnFirstFix {
            GlobalScope.launch(Dispatchers.Main) {
                currenLocation = myLocationOverlay?.myLocation
                mapController.setCenter(currenLocation)
                mapController.animateTo(currenLocation, null, 800L)

                mapView.overlays.add(myLocationOverlay)
                mapController.setZoom(5.0)

            }
        }
    }

    private fun requestLocationPermissions() {
        requestPermision.launch(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    private fun initOSMConfiguration() {

        //Config to save map data
        Configuration.getInstance().userAgentValue =
            requireActivity().packageName

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        mapController = mapView.controller
        mapController.setZoom(4.0)

        if (isLocationPermissionGranted()) {
            val locationProvider =
                GpsMyLocationProvider(requireContext())

            myLocationOverlay = MyLocationNewOverlay(
                locationProvider, mapView
            )
        } else {
            //TODO request permission
        }

    }

    private fun addMarkerOnMap(geoPoint: GeoPoint) {

        tmpMarker = Marker(mapView).apply {
            position = geoPoint
            icon = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_my_location_pin
            )
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            isMarkerSaved = false

            mapView.overlays.add(this)
            mapController.animateTo(geoPoint, null, 800L)
            mapView.invalidate()
        }

        homeViewModel.addMarkerGeoPoint(geoPoint)

        showNewMarkerInfoSheet()

    }

    private fun showMarkerInfoSheet(note: Notes) {
        val bottomSheet = MarkerInfoSheetFragment()

        val mNote = Notes(
            userName = note.userName,
            title = note.title,
            dateTime = if (note.dateTime.isEmpty()) "" else MyDateTime.convertUTCtoLocalTime(
                note.dateTime
            ),

            description = note.description,
            latitude = note.latitude,
            longitude = note.longitude
        )

        homeViewModel.onSendMarkerData(mNote)

        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private val markerNewSheet = MarkerNewSheetFragment().apply {
        onDismissListener = actionWhenDismiss()
    }

    private fun showNewMarkerInfoSheet() {

        markerNewSheet.show(
            parentFragmentManager, markerNewSheet.tag
        )

    }

    private fun actionWhenDismiss(): (() -> Unit) {
        return {
            if (!isMarkerSaved) {
                tmpMarker?.remove(mapView)
                mapView.invalidate()
            }
        }

    }

    private fun observerState() {
        //observe load all notes from server
        homeViewModel.allNotes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> addNotesItemsToMap(state.data)
                else -> {}
            }
        }

        //observe when add notes to server
        homeViewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    isMarkerSaved = true
                    tmpMarker = null
                    loadNotesData()
                    markerNewSheet.dismiss()

                }

                else -> isMarkerSaved = false
            }
        }

        //observe data from MarkerNewSheet
        homeViewModel.markerInfo.observe(viewLifecycleOwner) {

            if (it.title.isNotEmpty()) {
                tmpMarker?.title = it.title
                tmpMarker?.subDescription = it.desc
                tmpMarker?.snippet = userName
                Log.d("MY_TAG", "WTH!!!")

            }

        }

        homeViewModel.searchNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Success -> showDropdownNotes(state.data)
                is UiState.Failure -> Toast.makeText(
                    context, "${state.error}", Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun showDropdownNotes(data: List<Notes>) {
        adapter = SearchArrayAdapter(
            requireContext(), R.layout.search_dropdown_item, data
        )

        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.showDropDown()

    }

    private fun addNotesItemsToMap(noteList: List<Notes>) {
        for (note in noteList) {

            if (note.latitude != 0.0 && note.longitude != 0.0) {
                val noteMarker = Marker(mapView).apply {
                    position = GeoPoint(
                        note.latitude, note.longitude
                    )

                    icon =
                        if (note.userName == userName) ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_my_location_pin
                        ) else ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_flag
                        )
                    title = note.title
                    subDescription = note.description
                    snippet = note.userName

                    setAnchor(
                        Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
                    )
                }

                noteMarker.setOnMarkerClickListener { _, _ ->

                    showMarkerInfoSheet(note)
                    true
                }

                mapView.overlays.add(noteMarker)
                mapView.invalidate()

            }

        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        Log.d("TAGGG", "PAUSE")
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        Log.d("TAGGG", "RESUME")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TAGGG", "DESTROY")
        mapView.onDetach()
        myLocationOverlay?.disableMyLocation()
        _binding = null
    }

    private fun showDialog(title: String, message: String) {

        AlertDialog.Builder(requireContext()).setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Go to App Settings") { _, _ -> activity?.openAppSetting() }
            .show()
    }

}

fun Activity.openAppSetting() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also { startActivity(it) }
}

