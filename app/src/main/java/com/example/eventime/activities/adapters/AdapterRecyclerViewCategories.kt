package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.listeners.ClickListener
import kotlinx.android.synthetic.main.item_category.view.*
import org.jetbrains.anko.find

class AdapterRecyclerViewCategories(private val categories: ArrayList<String>, private val clickListener: ClickListener): RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)

        return CategoryViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    fun asd(){

    }
}

class CategoryViewHolder(view: View, private val clickListener: ClickListener): RecyclerView.ViewHolder(view),
        View.OnClickListener {
    var categoryIcon: ImageView = view.find(R.id.item_category_iv_category_icon)
    private var categoryTitle: TextView = view.find(R.id.item_category_tv_category_title)

    init {
        view.setOnClickListener(this)
    }

    fun bind(category: String) {
        categoryTitle.text = category
        //categoryIcon.
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}