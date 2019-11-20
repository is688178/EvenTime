package com.example.eventime.activities.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventime.R
import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.listeners.ClickListener
import kotlinx.android.synthetic.main.item_category.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.image
import java.io.File

class AdapterRecyclerViewCategories(private val categories: ArrayList<Category>, private val clickListener: ClickListener): RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)

        return CategoryViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }
}

class CategoryViewHolder(val view: View, private val clickListener: ClickListener): RecyclerView.ViewHolder(view),
        View.OnClickListener {
    var categoryIcon: ImageView = view.find(R.id.item_category_iv_category_icon)
    private var categoryTitle: TextView = view.find(R.id.item_category_tv_category_title)

    init {
        view.setOnClickListener(this)
    }

    fun bind(category: Category) {
        categoryTitle.text = category.name
        categoryIcon.background = if(category.selected) {
            view.context.getDrawable(R.drawable.background_white_circle_category)
        } else {
            view.context.getDrawable(R.drawable.background_dark_gray_circle_category)
        }
        if (category.name == "Todos") {
            Glide.with(view.context)
                .load(R.drawable.ic_hamburger)
                .override(80, 80)
                .into(categoryIcon)
        } else if (category.iconParseFile != null && category.iconWParseFile != null) {
            val url = if(category.selected) category.iconWParseFile.url else category.iconParseFile.url
            Glide.with(view.context)
                .load(url)
                .override(80, 80)
                .into(categoryIcon)
        }


        /*val iconUri = Uri.fromFile(category.icon)
        val inpStream = view.context.contentResolver.openInputStream(iconUri)
        categoryIcon.image = Drawable.createFromStream(inpStream, "")*/
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}