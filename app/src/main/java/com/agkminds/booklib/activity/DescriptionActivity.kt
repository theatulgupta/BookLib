package com.agkminds.booklib.activity


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase
import com.agkminds.booklib.R
import com.agkminds.booklib.adapter.DashboardRecyclerAdapter
import com.agkminds.booklib.database.BookDatabase
import com.agkminds.booklib.database.BookEntity
import com.agkminds.booklib.databinding.ActivityDescriptionBinding
import com.agkminds.booklib.databinding.FragmentFavouritesBinding
import com.agkminds.booklib.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding

    var bookId: String? = "100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

//        Setting up the toolbar & name
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Book Details"

        binding.progressBar.visibility = View.VISIBLE

        if (intent != null) {
            bookId = intent.getStringExtra("book_id").toString()
            if (bookId == "100") {
                Toast.makeText(this, "Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
            finish()
        }

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParam = JSONObject()
        jsonParam.put("book_id", bookId)

        if (ConnectionManager().isNetworkAvailable(this)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParam, Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJSONObject = it.getJSONObject("book_data")
                            binding.progressBar.visibility = View.GONE

                            val bookImgUrl = bookJSONObject.getString("image")
//                        Loading book image from the API to imgBook
                            Picasso.get().load(bookJSONObject.getString("image"))
                                .error(R.drawable.ic_book2).into(binding.imgBook)

                            binding.txtBookName.text = bookJSONObject.getString("name")
                            binding.txtAuthorName.text = bookJSONObject.getString("author")
                            binding.txtRating.text = bookJSONObject.getString("rating")
                            binding.txtPrice.text = bookJSONObject.getString("price")
                            binding.txtBookDescription.text =
                                bookJSONObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                binding.txtBookName.text.toString(),
                                binding.txtAuthorName.text.toString(),
                                binding.txtRating.text.toString(),
                                binding.txtPrice.text.toString(),
                                binding.txtBookDescription.text.toString(),
                                bookImgUrl
                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get() // This will return the true or false

//                            Getting a color using ContextCompat
                            if (isFav) {
                                binding.btnAddToFav.text = "Remove from Favourites"
                                val favColor =
                                    ContextCompat.getColor(applicationContext, R.color.DarkBlue)
                                binding.btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                binding.btnAddToFav.text = "Add to Favourites"
                                val noFavColor =
                                    ContextCompat.getColor(applicationContext, R.color.LightOrange)
                                binding.btnAddToFav.setBackgroundColor(noFavColor)
                            }

                            binding.btnAddToFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val asyncResult =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                            .get()
                                    if (asyncResult) {
                                        Toast.makeText(this,
                                            "Book added to favourites",
                                            Toast.LENGTH_SHORT).show()

                                        binding.btnAddToFav.text = "Remove from Favourites"
                                        val favColor =
                                            ContextCompat.getColor(applicationContext,
                                                R.color.DarkBlue)
                                        binding.btnAddToFav.setBackgroundColor(favColor)
                                    } else {
                                        Toast.makeText(this,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val asyncResult =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                            .get()
                                    if (asyncResult) {
                                        Toast.makeText(this,
                                            "Book removed from favourites",
                                            Toast.LENGTH_SHORT).show()

                                        binding.btnAddToFav.text = "Add to Favourites"
                                        val noFavColor =
                                            ContextCompat.getColor(applicationContext,
                                                R.color.LightOrange)
                                        binding.btnAddToFav.setBackgroundColor(noFavColor)
                                    } else {
                                        Toast.makeText(this,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this, "Some unexpected error occurred!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                    Response.ErrorListener {
                        if (this@DescriptionActivity != null)
                            Toast.makeText(this, "Volley error occurred", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4fb0bb6a0f10ec"
                        return headers
                    }
                }
            queue.add(jsonRequest) // Adding JSON POST request to queue
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("No Internet Connectivity")
            dialog.setMessage("Please check your internet connection and try again")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    //    This AsyncTask class is deprecated, so we are using here it only for training purpose.
//    Projects on future will be made using the Kotlin Coroutines

    class DBAsyncTask(
        val context: Context,
        private val bookEntity: BookEntity,
        private val mode: Int,
    ) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        MODE 1 ->   Check DB if the book is favourite or not.
        MODE 2 ->   Save the book into DB as favourite
        MODE 3 ->   Remove the favourite book
        */

        //        Initializing the DB object
        private val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> { // Check DB if the book is favourite or not.
                    val book: BookEntity = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }
                2 -> { // Save the book into DB as favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> { // Remove the favourite book
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}