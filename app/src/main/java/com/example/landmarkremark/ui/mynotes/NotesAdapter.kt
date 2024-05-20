package com.example.landmarkremark.ui.mynotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.landmarkremark.databinding.NoteItemBinding
import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.utils.MyDateTime

class NotesAdapter(
    val onItemClicked: (Int, Notes) -> Unit
) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var notesList: MutableList<Notes> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotesAdapter.NotesViewHolder {
        val itemView = NoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(
        holder: NotesViewHolder,
        position: Int
    ) {
        val item = notesList[position]
        holder.bind(item)

    }

    fun remoteNote(position: Int) {
        notesList.removeAt(position)
        notifyItemChanged(position)
    }

    fun loadNoteList(list: MutableList<Notes>) {
        this.notesList = list
        notifyDataSetChanged()
    }

    inner class NotesViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notes) {
            binding.txtTitle.text = item.title
            binding.txtDateTime.text =
                MyDateTime.convertUTCtoLocalTime(item.dateTime)

            binding.txtGeoPoint.text =
                "(${item.latitude}, ${item.longitude})"
            binding.txtDesc.apply {
                text = if (item.description.length > 120) {
                    "${item.description.substring(0, 120)}..."
                } else {
                    item.description
                }
            }
            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(
                    absoluteAdapterPosition,
                    item
                )
            }
        }
    }
}