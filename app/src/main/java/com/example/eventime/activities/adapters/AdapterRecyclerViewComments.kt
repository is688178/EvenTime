package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.beans.Comment
import com.example.eventime.activities.listeners.ClickListener
import org.jetbrains.anko.find

class AdapterRecyclerViewComments(private val comments: ArrayList<Comment>, private val clickListener: ClickListener):
    RecyclerView.Adapter<CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

        return CommentViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }
}

class CommentViewHolder(private val view: View, private val clickListener: ClickListener): RecyclerView.ViewHolder(view),
        View.OnClickListener {

    private val ivPersonPhoto: ImageView = view.find(R.id.item_comment_iv_person_photo)
    private val tvPersonName: TextView = view.find(R.id.item_comment_tv_person_name)
    private val tvCommentDate: TextView = view.find(R.id.item_comment_tv_comment_date)
    private val rbEventRating: RatingBar = view.find(R.id.item_comment_rb_event_rating)
    private val tvCommentDescription: TextView = view.find(R.id.item_comment_tv_comment_description)

    init {
        view.setOnClickListener(this)
    }

    fun bind(comment: Comment) {
        /*Glide
            .with(view.context)
            .load(comment.person.photo)
            .circleCrop()
            .into(ivPersonPhoto)*/

        tvPersonName.text = "${comment.person.name} ${comment.person.lastname}"
        tvCommentDate.text = comment.date
        rbEventRating.rating = comment.eventRating.toFloat()
        //tvCommentDescription.text = comment.description
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}