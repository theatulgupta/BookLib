package com.agkminds.booklib.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agkminds.booklib.R
import com.agkminds.booklib.activity.DescriptionActivity
import com.agkminds.booklib.model.Book
import com.squareup.picasso.Picasso
import java.util.ArrayList

class DashboardRecyclerAdapter(
    private val context: Context,
    private val itemList: ArrayList<Book>,
) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtAuthorName)
        val txtBookPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgBook)
        val bookItemLayout: ConstraintLayout = view.findViewById(R.id.bookItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookRating.text = book.bookRating
        holder.txtBookPrice.text = book.bookPrice

//        Using the Picasso-Library to Load Image from URL
//        The error(default_image) method loads a default image if error occurs in loading image using the Picasso Library
        Picasso.get().load(book.bookImage).error(R.drawable.ic_book2).into(holder.imgBookImage)

        holder.bookItemLayout.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}