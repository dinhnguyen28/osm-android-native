package com.example.landmarkremark.ui.map

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.landmarkremark.R
import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.utils.MyDateTime

class SearchArrayAdapter(
    context: Context,
    private val resources: Int,
    private val notes: List<Notes>,
) : ArrayAdapter<Notes>(context, resources, notes) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(resources, parent, false)


        if (notes.isNotEmpty()) {
            val item = notes[position]

            val title: TextView =
                view.findViewById(R.id.dropdown_title)
            val description: TextView =
                view.findViewById(R.id.dropdown_desc)
            val geoPoint: TextView =
                view.findViewById(R.id.dropdown_gpoint)
            val username: TextView =
                view.findViewById(R.id.dropdown_username)
            val datetime: TextView =
                view.findViewById(R.id.dropdown_datetime)



            title.text = item.title
            description.text = item.description
            geoPoint.text = context.getString(
                R.string.geo_point,
                item.latitude,
                item.longitude
            )
            username.text = item.userName
            datetime.text = MyDateTime.convertUTCtoLocalTime(item.dateTime)

        } else {
            Log.d("note_lists", "note list is empty")
        }

        return view
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return getView(position, convertView, parent)
    }
}