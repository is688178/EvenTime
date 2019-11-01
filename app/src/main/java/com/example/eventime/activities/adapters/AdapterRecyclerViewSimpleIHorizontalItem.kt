package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.listeners.ClickListener
import kotlinx.android.synthetic.main.item_date_or_hour.view.*
import org.jetbrains.anko.find

class AdapterSimpleItemHorizontal(private val items: ArrayList<String>, private val clickListener: ClickListener): RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_or_hour, parent, false)

        return ItemViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class ItemViewHolder(view: View, private val clickListener: ClickListener): RecyclerView.ViewHolder(view),
        View.OnClickListener {
    private val tvConetent = view.find<TextView>(R.id.item_date_or_hour_tv_content)

    init {
        view.setOnClickListener(this)
    }

    fun bind(item: String) {
        tvConetent.text = item
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}