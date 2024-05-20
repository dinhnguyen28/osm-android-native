package com.example.landmarkremark.ui.bottomsheet.markerinfo

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.landmarkremark.R
import com.example.landmarkremark.databinding.FragmentMarkerInfoSheetBinding
import com.example.landmarkremark.ui.map.HomeViewModel
import com.example.landmarkremark.utils.MyDateTime

class MarkerInfoSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMarkerInfoSheetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMarkerInfoSheetBinding.inflate(
            inflater,
            container,
            false
        )




        return binding.root

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        homeViewModel.markerData.observe(this) {

            binding.markerUsername.text = it.userName
            binding.markerTitle.text = it.title
            binding.markerDesc.text = it.description
            binding.markerGeoPoint.text = getString(
                R.string.geo_point, it.latitude,
                it.longitude
            )

            binding.markerDateTime.text =
                MyDateTime.convertUTCtoLocalTime(it.dateTime)




        }



        binding.btnDelMarker.setOnClickListener { deleteMarker() }

    }

    private fun deleteMarker() {
//        homeViewModel.

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}