package com.example.landmarkremark.ui.bottomsheet.markernew

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.landmarkremark.MainActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.databinding.FragmentMarkerNewSheetBinding
import com.example.landmarkremark.models.MarkerInfo
import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.ui.map.HomeViewModel
import com.example.landmarkremark.utils.MyDateTime
import com.example.landmarkremark.utils.UiState

import org.osmdroid.util.GeoPoint

class MarkerNewSheetFragment : BottomSheetDialogFragment() {

    var onDismissListener: (() -> Unit)? = null

    private var _binding: FragmentMarkerNewSheetBinding? =
        null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    private var userName: String = ""

    private var geoPoint: GeoPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogStyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkerNewSheetBinding.inflate(
            inflater, container, false
        )


        userName = (activity as? MainActivity)?.getUserName() ?: ""




        homeViewModel.geoPoint.observe(this) {

            geoPoint = it
            binding.txtGeoPoint.text = getString(
                R.string.geo_point,
                it.latitude,
                it.longitude
            )

        }


        Log.d("ARGRUMENT", arguments?.getString(ARG_DATA).toString())

        return binding.root

    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {

        observerState()

        binding.btnSaveMarker.setOnClickListener { saveMarkerToDb() }
    }

    private fun saveMarkerToDb() {
        val title = binding.edtMarkerTitle.text.toString()
        val desc = binding.edtMarkerDesc.text.toString()

        val currentTimeUTC = MyDateTime.getCurrentTimeUTC()

        Log.d("TAGG", currentTimeUTC)

        val mNote = Notes(
            userName = userName,
            title = title,
            description = desc,
            latitude = geoPoint?.latitude ?: 0.0,
            longitude = geoPoint?.longitude ?: 0.0,
        )

        if (title.isNotEmpty()) {
            homeViewModel.onSaveMarkerInfo(
                MarkerInfo(title, desc),
                mNote
            )
        }

    }

    private fun observerState() {
        binding.progessBar.max = 1000


        homeViewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {

                is UiState.Loading -> {
                    binding.progessBar.visibility = View.VISIBLE

                    ObjectAnimator.ofInt(
                        binding.progessBar,
                        "progress",
                        1000
                    )
                        .setDuration(1000L).start()

                    isMakeEnableUI(false)

                    Log.d("TAG", "loading")
                }

                is UiState.Failure -> {
                    binding.progessBar.visibility = View.INVISIBLE
                }

                is UiState.Success -> {

                    binding.progessBar.visibility = View.INVISIBLE

                    isMakeEnableUI(true)
                    Log.d("TAG", "Success")
                    binding.edtMarkerTitle.setText("")
                    binding.edtMarkerDesc.setText("")

                }

                else -> {}

            }
        }
    }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.edtMarkerTitle.isEnabled = isDisable
        binding.edtMarkerDesc.isEnabled = isDisable
        binding.btnSaveMarker.isEnabled = isDisable

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDismissListener?.invoke()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_DATA = "data"

        fun newInstance(data: String): MarkerNewSheetFragment {
            val fragment = MarkerNewSheetFragment()
            val args = Bundle()
            args.putString(ARG_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }

}