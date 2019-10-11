package com.example.eventime.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import org.jetbrains.anko.find

class AdapterRecyclerViewCategories(private val categories: ArrayList<String>): RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)

        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }
}

class CategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
    //private var categoryIcon: ImageView = view.find(R.id.item_category_iv_category_icon)
    private var categoryTitle: TextView = view.find(R.id.item_category_tv_category_title)

    fun bind(category: String) {
        categoryTitle.text = category
        //categoryIcon.
    }
}