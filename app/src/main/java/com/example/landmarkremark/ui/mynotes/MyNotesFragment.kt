package com.example.landmarkremark.ui.mynotes

import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.landmarkremark.MainActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.databinding.FragmentMyNotesBinding
import com.example.landmarkremark.ui.login.LoginResult
import com.example.landmarkremark.ui.login.LoginViewModel
import com.example.landmarkremark.ui.map.HomeViewModel
import com.example.landmarkremark.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyNotesFragment : Fragment() {

    private var _binding: FragmentMyNotesBinding? = null
    private val homeViewModel: HomeViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var userName: String = ""

    private val adapter by lazy {
        NotesAdapter(
            onItemClicked = { pos, item ->
                Log.d("item_click", "$pos : $item")
                Toast.makeText(context, "$pos: (${item.latitude}, ${item.longitude})", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyNotesBinding.inflate(
            inflater,
            container,
            false
        )

        userName = (activity as? MainActivity)?.getUserName() ?: ""
        homeViewModel.getMyNotes(username = userName)



        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        oberverMyNotes()

        val staggeredLinearLayoutManager = StaggeredGridLayoutManager(
            1,
            LinearLayoutManager.VERTICAL
        )
        binding.recycleView.layoutManager =
            staggeredLinearLayoutManager

        binding.recycleView.adapter = adapter

    }

    private fun oberverMyNotes() {
        homeViewModel.myNotes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Failure -> {
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(
                        context,
                        state.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.loadNoteList(state.data.toMutableList())
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}