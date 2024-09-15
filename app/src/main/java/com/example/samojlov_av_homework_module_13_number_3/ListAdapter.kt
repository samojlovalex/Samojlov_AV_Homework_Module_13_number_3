package com.example.samojlov_av_homework_module_13_number_3

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListAdapter(context: Context, listItem: MutableList<Person>) :
    ArrayAdapter<Person>(context, R.layout.list_item, listItem) {
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val person = getItem(position)
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val name = view?.findViewById<TextView>(R.id.nameListItemTV)
        val surname = view?.findViewById<TextView>(R.id.surnameListItemTV)
        val age = view?.findViewById<TextView>(R.id.ageListItemTV)
        val post = view?.findViewById<TextView>(R.id.postListItemTV)

        name?.text = person?.name
        surname?.text = person?.surname
        age?.text = "${person?.age} лет"
        post?.text = person?.post

        return view!!
    }
}